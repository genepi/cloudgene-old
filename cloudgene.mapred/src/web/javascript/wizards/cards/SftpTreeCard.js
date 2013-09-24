Ext.ns('MapRed.wizards');

MapRed.wizards.SftpTreeCard = Ext.extend(Ext.ux.Wiz.Card, {

	serverField : null,

	infoText : null,

	folder: "",

	initComponent : function() {

		Ext.apply(this, {
			id : 'card2',
			wizRef : this,
			title : 'Import from sftp server.',
			monitorValid : true,
			frame : false,
			fileUpload : true,
			border : false,
			height : '100%',
			folder: this.folder,
			defaults : {
				labelStyle : 'font-size:11px'
			},
			items : [ {
				title : '',
				id : 'fieldset-target',
				xtype : 'fieldset',
				autoHeight : true,
				defaults : {
					width : 210,
					labelStyle : 'font-size:11px'
				},
				defaultType : 'textfield',
				items : [ new Ext.form.TextField({
					id : 'path',
					fieldLabel : 'Folder Name',
					allowBlank : false,
					value: this.folder
				}) ]
			}, {
				title : 'Sftp server',
				id : 'fieldset-amazon',
				xtype : 'fieldset',
				height : 250,
				defaults : {
					width : 210,
					labelStyle : 'font-size:11px'
				},
				items : [ new Ext.form.TextField({
					id : "server",
					name : 'server',
					value : '',
					fieldLabel : 'Folder',
					allowBlank : false,
					readOnly : true,
					hidden : true
				}), new Ext.tree.TreePanel({
					id : 'file-tree',
					useArrows : true,
					autoScroll : true,
					animate : false,
					enableDD : false,
					containerScroll : true,
					allowBlank : false,
					collapsible : false,
					animCollapse : false,
					anchor : '100%',
					border : false,
					height: 200,
					border : false,

					loader : new Ext.tree.TreeLoader({
						error : false,
						dataUrl : '../sftp/files'
						handleFailure : function(
							response) {
						Ext.Msg
						    .alert(
						    'Error',
						    'Connecting to SFTP Server.');
							this.error = true;		
					}),

					root : new Ext.tree.AsyncTreeNode({
						id : 'root',
						text : 'Files',
						draggable : false
					}),

					viewConfig : {
						forceFit : true
					},

					listeners : {
						click : {
							fn : this.clickListener
						}
					}

				}) ]
			} ]
		});

		// call parent
		MapRed.wizards.ImportLocalFileCard.superclass.initComponent.apply(this,
				arguments);
	},

	clickListener : function(node, event) {

		Ext.getCmp('server').setValue(node.attributes.path);

	}
});
