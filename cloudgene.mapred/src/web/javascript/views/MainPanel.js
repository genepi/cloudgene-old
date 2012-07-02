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

MapRed.view.MainPanel = Ext.extend(Ext.Panel, {
    initComponent : function() {

	Ext.apply(this, {
	    layout : 'border',
	    id : 'vp',

	    items : [ {
		region : "north",
		xtype : 'mytoolbar',
		height : 65,
		split : true
	    }, {
		region : 'center',
		xtype : 'jobtable',
		id : 'jobtable',
		layout : 'fit',
		border : false
	    }, {
		region : 'south',
		height : 400,
		split : true,
		header : false,
		collapsible : true,
		collapseMode : 'mini',
		autoScroll: true,
		items : [ {
		    region : 'south',
		    preventBodyReset : true,
		    border : true,
		    id : 'detailspanel',
		    xtype : 'detailspanel'
		} ]

	    } ]
	});

	// call parent
	MapRed.view.MainPanel.superclass.initComponent.apply(this, arguments);

    }

});
Ext.reg('mainpanel', MapRed.view.MainPanel);