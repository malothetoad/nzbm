/*
 * This file is part of nzbget
 *
 * Copyright (C) 2012 Andrey Prygunkov <hugbug@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * $Revision: 488 $
 * $Date: 2012-11-03 08:41:44 +0100 (sam. 03 nov. 2012) $
 *
 */

/*
 * In this module:
 *   1) History tab;
 *   2) History edit dialog.
 */
 
/*** HISTORY TAB AND EDIT HISTORY DIALOG **********************************************/
 
var History = (new function($)
{
	'use strict';
 
	// Controls
	var $HistoryTable;
	var $HistoryTabBadge;
	var $HistoryTabBadgeEmpty;
	var $HistoryRecordsPerPage;

	// State
	var history;
	var notification = null;
	var updateTabInfo;

	this.init = function(options)
	{
		updateTabInfo = options.updateTabInfo;

		$HistoryTable = $('#HistoryTable');
		$HistoryTabBadge = $('#HistoryTabBadge');
		$HistoryTabBadgeEmpty = $('#HistoryTabBadgeEmpty');
		$HistoryRecordsPerPage = $('#HistoryRecordsPerPage');
		
		historyEditDialog.init();

		var recordsPerPage = UISettings.read('HistoryRecordsPerPage', 10);
		$HistoryRecordsPerPage.val(recordsPerPage);

		$HistoryTable.fasttable(
			{
				filterInput: $('#HistoryTable_filter'),
				filterClearButton: $("#HistoryTable_clearfilter"),
				pagerContainer: $('#HistoryTable_pager'),
				infoContainer: $('#HistoryTable_info'),
				headerCheck: $('#HistoryTable > thead > tr:first-child'),
				filterCaseSensitive: false,
				pageSize: recordsPerPage,
				maxPages: UISettings.miniTheme ? 1 : 5,
				pageDots: !UISettings.miniTheme,
				fillFieldsCallback: fillFieldsCallback,
				renderCellCallback: renderCellCallback,
				updateInfoCallback: updateInfo
			});

		$HistoryTable.on('click', 'a', editClick);
		$HistoryTable.on('click', 'tbody div.check',
			function(event) { $HistoryTable.fasttable('itemCheckClick', this.parentNode.parentNode, event); });
		$HistoryTable.on('click', 'thead div.check',
			function() { $HistoryTable.fasttable('titleCheckClick') });
		$HistoryTable.on('mousedown', Util.disableShiftMouseDown);
	}

	this.applyTheme = function()
	{
		$HistoryTable.fasttable('setPageSize', UISettings.read('HistoryRecordsPerPage', 10),
			UISettings.miniTheme ? 1 : 5, !UISettings.miniTheme);
	}

	this.update = function()
	{
		RPC.call('history', [], loaded);
	}

	function loaded(curHistory)
	{
		history = curHistory;
		prepare();
		RPC.next();
	}

	function prepare()
	{
		for (var j=0, jl=history.length; j < jl; j++)
		{
			detectStatus(history[j]);
		}
	}

	function detectStatus(hist)
	{
		if (hist.Kind === 'NZB')
		{
			switch (hist.ScriptStatus)
			{
				case 'SUCCESS': hist.status = 'success'; break;
				case 'FAILURE': hist.status = 'failure'; break;
				case 'UNKNOWN': hist.status = 'unknown'; break;
				case 'NONE':
					switch (hist.ParStatus)
					{
						case 'SUCCESS': hist.status = 'success'; break;
						case 'REPAIR_POSSIBLE': hist.status = 'repairable'; break;
						case 'FAILURE': hist.status = 'failure'; break;
						case 'NONE': hist.status = 'none'; break;
					}
			}
		}
		else if (hist.Kind === 'URL')
		{
			switch (hist.UrlStatus)
			{
				case 'SUCCESS': hist.status = 'success'; break;
				case 'FAILURE': hist.status = 'failure'; break;
				case 'UNKNOWN': hist.status = 'unknown'; break;
			}
		}
	}

	this.redraw = function()
	{
		var data = [];

		for (var i=0; i < history.length; i++)
		{
			var hist = history[i];

			var kind = hist.Kind;
			var statustext = hist.status === 'none' ? 'unknown' : hist.status;
			var size = kind === 'NZB' ? Util.formatSizeMB(hist.FileSizeMB) : '';

			var textname = hist.Name;
			if (kind === 'URL')
			{
				textname += ' URL';
			}

			var time = Util.formatDateTime(hist.HistoryTime + UISettings.timeZoneCorrection*60*60);

			var item =
			{
				id: hist.ID,
				hist: hist,
				data: {time: time, size: size},
				search: statustext + ' ' + time + ' ' + textname + ' ' + hist.Category + ' ' + size
			};

			data.push(item);
		}

		$HistoryTable.fasttable('update', data);

		Util.show($HistoryTabBadge, history.length > 0);
		Util.show($HistoryTabBadgeEmpty, history.length === 0 && UISettings.miniTheme);
	}

	function fillFieldsCallback(item)
	{
		var hist = item.hist;

		var status = buildStatus(hist);

		var name = '<a href="#" histid="' + hist.ID + '">' + Util.textToHtml(Util.formatNZBName(hist.Name)) + '</a>';
		var category = Util.textToHtml(hist.Category);

		if (hist.Kind === 'URL')
		{
			name += ' <span class="label label-info">URL</span>';
		}

		if (!UISettings.miniTheme)
		{
			item.fields = ['<div class="check img-check"></div>', status, item.data.time, name, category, item.data.size];
		}
		else
		{
			var info = '<div class="check img-check"></div><span class="row-title">' + name + '</span>' +
				' ' + status + ' <span class="label">' + item.data.time + '</span>';
			if (category)
			{
				info += ' <span class="label label-status">' + category + '</span>';
			}
			if (hist.Kind === 'NZB')
			{
				info += ' <span class="label">' + item.data.size + '</span>';
			}
			item.fields = [info];
		}
	}

	function renderCellCallback(cell, index, item)
	{
		if (index === 2)
		{
			cell.className = 'text-center';
		}
		else if (index === 5)
		{
			cell.className = 'text-right';
		}
	}

	function buildStatus(hist)
	{
		switch (hist.status)
		{
			case 'success': return '<span class="label label-status label-success">success</span>';
			case 'failure': return '<span class="label label-status label-important">failure</span>';
			case 'unknown': return '<span class="label label-status label-info">unknown</span>';
			case 'repairable': return '<span class="label label-status label-success">repairable</span>';
			case 'none': return '<span class="label label-status">unknown</span>';
			default: return '<span class="label label-status label-important">internal error(' + hist.status + ')</span>';
		}
	}

	this.recordsPerPageChange = function()
	{
		var val = $HistoryRecordsPerPage.val();
		UISettings.write('HistoryRecordsPerPage', val);
		$HistoryTable.fasttable('setPageSize', val);
	}

	function updateInfo(stat)
	{
		updateTabInfo($HistoryTabBadge, stat);
	}

	this.deleteClick = function()
	{
		if (history.length == 0)
		{
			return;
		}

		var checkedRows = $HistoryTable.fasttable('checkedRows');
		if (checkedRows.length > 0)
		{
			ConfirmDialog.showModal('HistoryDeleteConfirmDialog', historyDelete);
		}
		else
		{
			ConfirmDialog.showModal('HistoryClearConfirmDialog', historyClear);
		}
	}

	function historyDelete()
	{
		Refresher.pause();

		var IDs = $HistoryTable.fasttable('checkedRows');

		RPC.call('editqueue', ['HistoryDelete', 0, '', [IDs]], function()
		{
			notification = '#Notif_History_Deleted';
			editCompleted();
		});
	}

	function historyClear()
	{
		Refresher.pause();

		var IDs = [];
		for (var i=0; i<history.length; i++)
		{
			IDs.push(history[i].ID);
		}

		RPC.call('editqueue', ['HistoryDelete', 0, '', [IDs]], function()
		{
			notification = '#Notif_History_Cleared';
			editCompleted();
		});
	}

	function editCompleted()
	{
		Refresher.update();
		if (notification)
		{
			Notification.show(notification);
			notification = null;
		}
	}
	
	function editClick()
	{
		var histid = $(this).attr('histid');
		$(this).blur();
		historyEditDialog.showModal(histid);
	}
	
/*** EDIT HISTORY DIALOG *************************************************************************/

	var historyEditDialog = new function()
	{
		// Controls
		var $HistoryEditDialog;

		// State
		var curHist;

		this.init = function()
		{
			$HistoryEditDialog = $('#HistoryEditDialog');
			
			$('#HistoryEdit_Delete').click(itemDelete);
			$('#HistoryEdit_Return').click(itemReturn);
			$('#HistoryEdit_Reprocess').click(itemReprocess);

			$HistoryEditDialog.on('hidden', function () {
				Refresher.resume();
			});
		}

		this.showModal = function(histid)
		{
			var hist = null;

			// find history object
			for (var i=0; i<history.length; i++)
			{
				var gr = history[i];
				if (gr.ID == histid)
				{
					hist = gr;
					break;
				}
			}
			if (hist == null)
			{
				return;
			}

			Refresher.pause();

			curHist = hist;

			var status = buildStatus(hist);

			$('#HistoryEdit_Title').text(Util.formatNZBName(hist.Name));
			if (hist.Kind === 'URL')
			{
				$('#HistoryEdit_Title').html($('#HistoryEdit_Title').html() + '&nbsp;' + '<span class="label label-info">URL</span>');
			}

			$('#HistoryEdit_Status').html(status);
			$('#HistoryEdit_Category').text(hist.Category !== '' ? hist.Category : '<empty>');
			$('#HistoryEdit_Path').text(hist.DestDir);

			var size = Util.formatSizeMB(hist.FileSizeMB, hist.FileSizeLo);

			var table = '';
			table += '<tr><td>Total</td><td class="text-right">' + size + '</td></tr>';
			table += '<tr><td>Files (total/parked)</td><td class="text-right">' + hist.FileCount + '/' + hist.RemainingFileCount + '</td></tr>';
			$('#HistoryEdit_Statistics').html(table);

			Util.show($('#HistoryEdit_ReturnGroup'), hist.RemainingFileCount > 0 || hist.Kind === 'URL');
			Util.show($('#HistoryEdit_PathGroup, #HistoryEdit_StatisticsGroup, #HistoryEdit_ReprocessGroup'), hist.Kind === 'NZB');

			enableAllButtons();

			$HistoryEditDialog.modal({backdrop: 'static'});
		}

		function disableAllButtons()
		{
			$('#HistoryEditDialog .modal-footer .btn').attr('disabled', 'disabled');
			setTimeout(function()
			{
				$('#HistoryEdit_Transmit').show();
			}, 500);
		}

		function enableAllButtons()
		{
			$('#HistoryEditDialog .modal-footer .btn').removeAttr('disabled');
			$('#HistoryEdit_Transmit').hide();
		}

		function itemDelete()
		{
			disableAllButtons();
			notification = '#Notif_History_Deleted';
			RPC.call('editqueue', ['HistoryDelete', 0, '', [curHist.ID]], completed);
		}

		function itemReturn()
		{
			disableAllButtons();
			notification = '#Notif_History_Returned';
			RPC.call('editqueue', ['HistoryReturn', 0, '', [curHist.ID]], completed);
		}

		function itemReprocess()
		{
			disableAllButtons();
			notification = '#Notif_History_Reproces';
			RPC.call('editqueue', ['HistoryProcess', 0, '', [curHist.ID]], completed);
		}

		function completed()
		{
			$HistoryEditDialog.modal('hide');
			editCompleted();
		}
	}();
}(jQuery));
