/*******************************************************************************
 * Cloudgene: A graphical MapReduce interface for cloud computing
 * 
 * Copyright (C) 2010, 2011 Sebastian Schoenherr, Lukas Forer
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

Ext.ns('MapRed.view');

var myReader = new Ext.data.JsonReader({
    fields : [ {
	name : 'name',
	type : 'string'
    }, {
	name : 'currentStep',
	type : 'string'
    }, {
	name : 'executionTime',
	type : 'long'
    }, {
	name : 'state',
	type : 'integer'
    }, {
	name : 'id',
	type : 'string'
    }, {

	name : 'map',
	type : 'integer'
    }, {

	name : 'reduce',
	type : 'integer'
    } ]
});

var myStore = new Ext.data.Store(
	{

	    autoDestroy : true,
	    url : '../jobs',

	    reader : myReader,

	    listeners : {
		'load' : function() {

		    for (i = 0; i < this.getCount(); i++) {

			record = this.getAt(i);
			state = record.get("state");

			if (state == 1 || state == 2 || state == 3) {

			    store = new Ext.data.Store(
				    {
					record : record,
					url : 'jobs/state',
					autoLoad : false,

					baseParams : {
					    'job_id' : record.get("id")
					},

					temp : this,
					reader : myReader,
					scope : this,
					id : 'store-' + record.get("id"),
					listeners : {
					    'load' : function() {

						if (this.getCount() > 0) {

						    newRecord = this.getAt(0);

						    newId = newRecord
							    .get("id");

						    tableStore = Ext.getCmp(
							    'jobtable')
							    .getStore();

						    index = tableStore.find(
							    "id", newId);

						    oldRecord = tableStore
							    .getAt(index);
						    this.update(oldRecord,
							    newRecord);

						    if (this
							    .needsUpdate(newRecord)) {

							var t = setTimeout(
								'Ext.StoreMgr.lookup("store-'
									+ newId
									+ '").reload()',
								5000);

						    }
						}

					    }
					},

					needsUpdate : function(record) {

					    return record.get("state") == 1
						    || record.get("state") == 2
						    || record.get("state") == 3;

					},

					update : function(oldRecord, newRecord) {

					    for (i = 0; i < this.reader.meta.fields.length; i++) {
						field = this.reader.meta.fields[i].name;
						value = newRecord.get(field);
						oldRecord.set(field, value);
					    }

					}

				    }

			    )
			    store.load();

			}

		    }

		}
	    }

	});

MapRed.view.JobTable = Ext.extend(Ext.grid.GridPanel, {
    initComponent : function() {

	Ext.apply(this, {
	    title : 'Jobs',
	    height: 300,
	    columns : [ {
		id : 'icon',
		header : "",
		width : 30,
		flex : 0,
		resizable : false,
		dataIndex : 'state',
		renderer : this.iconRenderer
	    }, {
		id : 'name',
		header : "Job Name",
		width : 600,
		flex : 1,
		renderer : this.jobTitleRenderer,
		dataIndex : 'name'
	    }, {
		header : 'Progress',
		dataIndex : 'executionTime',
		flex : 1,
		width : 140,
		renderer : this.progressRenderer,
		align : 'center'
	    }, {
		header : 'Execution Time',
		dataIndex : 'executionTime',
		flex : 1,
		width : 150,
		renderer : this.timeRenderer,
		align : 'center'
	    }, {
		header : 'State',
		dataIndex : 'state',
		renderer : this.stateRenderer,
		flex : 1,
		width : 150,
		align : 'center'
	    }, {
		header : '',
		dataIndex : 'currentStep',
		width : 200,
		renderer : this.downloadRenderer,
		align : 'center'
	    } ],

	    store : myStore,
	    // border : false,
	    forceFit : true

	});

	// call parent
	MapRed.view.JobTable.superclass.initComponent.apply(this, arguments);
    },

    // Renderer

    iconRenderer : function(value, p, record) {

	if (value == 1) {
	    return '<img src="images/job-waiting.png"/>';
	} else if (value == 2 || value == 3) {
	    return '<img src="images/job-running-2.gif"/>';
	} else if (value == 4) {
	    return '<img src="images/job-ok.png"/>';
	} else {
	    return '<img src="images/job-error.png"/>';
	}
    },

    jobTitleRenderer : function(value, p, record) {

	if (record.data.state == 2 || record.data.state == 3) {
	    return '<div style="height: 50px"><b>' + value + '</b><br>'+record.data.currentStep+'</div>';
	} else {
	    return value;
	}
    },

    progressRenderer : function(value, p, record) {

	if (record.data.state == 2 || record.data.state == 3) {

	    text = '';

	    if (record.data.map >= 0) {

		var id = Ext.id();
		(function() {
		    new Ext.ProgressBar({
			renderTo : id,
			value : record.data.map / 100.0
		    });
		}).defer(25);

		text += '<div id="' + id + '" ></div>';
	    }

	    if (record.data.reduce >= 0) {

		var id = Ext.id();
		(function() {
		    new Ext.ProgressBar({
			renderTo : id,
			value : record.data.reduce / 100.0
		    });
		}).defer(25);

		text += '<div id="' + id + '" ></div>';

	    }

	    return text;

	}

	else {

	    return '';

	}

    },

    downloadRenderer : function(value, p, record) {
	if (record.data.state == 4) {
	    return "-";
	} else if (record.data.state == 5) {
	    return "-";
	} else if (record.data.state == 6) {
	    return "-";
	} else {
	    return '<a href="javascript:cancelJob(\'' + record.data.id
		    + '\')">Cancel</a>';
	}
    },

    stateRenderer : function(value, p, record) {
	
	if (value == 1) {
	    return 'Waiting';
	} else if (value == 2) {
	    // running
	    return 'Running';
	} else if (value == 3) {
	    return 'Exporting Data';
	} else if (value == 4) {    
	    return 'Complete';
	} else if (value == 5) {
	    return 'Error';
	} else if (value == 6) {
	    return 'Canceled'
	} else {
	    return 'Error'
	}

    },

    timeRenderer : function(value, p, record) {

	var time = value;

	if (time <= 0 || record.data.state == 5 || record.data.state == 1 || record.data.state == 6) {
	    return '-';
	} else {

	    var h = (Math.floor((time / 1000) / 60 / 60));
	    var m = ((Math.floor((time / 1000) / 60)) % 60);

	    return (h > 0 ? h + ' h ' : '') + (m > 0 ? m + ' min ' : '')
		    + ((Math.floor(time / 1000)) % 60) + ' sec';

	}
    }

});

Ext.reg('jobtable', MapRed.view.JobTable);

cancelJob = function(id) {

    Ext.Msg.confirm('Cancel Job', 'Do you really want to cancel job "' + id + '"?', function(btn, text) {
	if (btn == 'yes') {
	    Ext.Ajax.request({
		url : '../jobs/cancel',
		params : {
		    id : id
		},
		success : function(response) {

		    var jobTable = Ext.getCmp('jobtable');
		    var storeJobs = jobTable.getStore();
		    storeJobs.reload();

		}
	    });
	}
    });

}
