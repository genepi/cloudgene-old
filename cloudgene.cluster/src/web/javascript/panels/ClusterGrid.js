/**
 * @author seppinho
 * @license gridinbl.js is licensed under the terms of the Open Source LGPL 3.0
 *          license. Commercial use is permitted to the extent that the
 *          code/component(s) do NOT become part of another Open Source or
 *          Commercially licensed development library or toolkit without
 *          explicit permission.
 * 
 * License details: http://www.gnu.org/licenses/lgpl.html
 */
var recordID;
// namespace definition
Ext.ns('Clusters');
Ext.BLANK_IMAGE_URL = './libs/ext-3.4.0/resources/images/default/s.gif';

// create the Data Store
var store = new Ext.data.Store({
	id : 'clusterGrid',
	// destroy the store if the grid is destroyed
	autoDestroy : true,
	// load Data from the restlet ressource
	url : '../getClusters',

	// specify a XmlReader (coincides with the XML format of the returned data)
	reader : new Ext.data.JsonReader({
		fields : [{
			name : 'name',
			type : 'string'
		}, {
			name : 'id2',
			type : 'int'
		}, {
			name : 'state',
			type : 'string'
		},  {
			name : 'ssh',
			type : 'string'
		}, {
			name : 'log',
			type : 'string'
		},  {
			name : 'type',
			type : 'string'
		}, {
			name : 'amount',
			type : 'integer'
		}, {
			name : 'webAddress',
			type : 'string'
		},
		{   name : 'startTime',
			type : 'long'
		},
		{   name : 'upTime',
			type : 'string'
		}]
	}),
	sortInfo : {
		field : 'startTime',
		direction : 'DESC'
	}
});

function jobWebaddressRenderer(value, p, record) {

	if (value !== "") {
		return '<a href="http://' + value
			+ '" target="_blank">http://' + value
			+ '</a>';
	}
}

function sshKeyRenderer(value, p, record) {
	if (value !== "") {
		return '<a href="downloadKey?id=' + value
			+ '">Download SSH Key</a>';
	}
}

function logRenderer(value, p, record) {
	if (value !== "") {
	return '<a href="downloadLog?id=' + value
	+ '" target="_blank">Log</a>';
	}
}

function jobTitleRenderer(value, p, record) {

	if (record.data.state === 'building' || record.data.state === 'copy user data' || record.data.state === 'destroying') {
		return '<table><tr><td><img src="images/busy.gif"/></td><td><b>' + value + '</b></td></tr></table>';
	} else if (record.data.state === 'waiting') {
		return '<table><tr><td><img src="images/queue.png"/></td><td><b>' + value + '</b></td></tr></table>';
	} else if (record.data.state === 'down') {
		return '<table><tr><td><img src="images/error.png"/></td><td><b>' + value + '</b></td></tr></table>';
	} else {
		return '<table><tr><td><img src="images/done.png"/></td><td>' + value + '</td></tr></table>';
	}
}


// Cluster queue
Clusters.current = Ext
	.extend(
		Ext.grid.GridPanel,
		{
			initComponent : function () {
				var config = {
						store : store,
						columns : [ {
							id : 'id2',
							hidden : true,
							header : "id2",
							width : 50,
							dataIndex : 'id2'
						}, {
							id : 'name',
							header : "Cluster Name",
							width : 40,
							renderer : jobTitleRenderer,
							dataIndex : 'name'
						}, {
							header : 'Web address',
							dataIndex : 'webAddress',
							width : 25,
							renderer : jobWebaddressRenderer,
							align : 'center'
						},{
							header : 'State',
							dataIndex : 'state',
							width : 25,
							align : 'center'
						}, {
							header : 'Instance type',
							dataIndex : 'type',
							width : 25,
							align : 'center'
						}, {
						    header : 'Amount of datanodes',
						    dataIndex : 'amount',
						    width : 25,
						    align : 'center'
						},{
							header : 'Cluster Up-Time',
							dataIndex : 'upTime',
							width : 40,
							align : 'center'
						},
						{
							header : 'SSH key',
							dataIndex : 'ssh',
							width : 25,
							renderer : sshKeyRenderer,
							align : 'center'
						},{
							header : 'Log',
							dataIndex : 'log',
							width : 25,
							renderer: logRenderer,
							align : 'center'
						},
						{
							header : 'Ordering',
							hidden : true,
							dataIndex : 'startTime',
							width : 25,
							align : 'center'
						}],
						sm : new Ext.grid.RowSelectionModel({
							singleSelect : true,
							listeners : {
								rowselect : function (smObj, rowIndex,
									record) {
									recordID = record.data.id2;
									recordName = record.data.name;
								}
							}
						}),
						viewConfig : {
							forceFit : true
						}
					};

						// apply config
				Ext.apply(this, Ext.apply(this.initialConfig, config));

						// call parent
				Clusters.current.superclass.initComponent.apply(this,
								arguments);
			}

		}
	);

Ext.reg('clusterQueue', Clusters.current);

function refreshJobs() {
	store.reload();
	// store1.reload();
	var t = setTimeout(refreshJobs, 50000);
}
refreshJobs();