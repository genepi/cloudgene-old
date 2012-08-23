Ext.ns('MapRed.utils');

MapRed.utils.HdfsFileDialog = Ext.extend(Ext.Window, {

	outputTextfield : '',

	folderDialog : true,

	openDialog : true,

	nameTextField : null,

	newNameTextField : null,

	form : null,

	initComponent : function() {


		// Name (only in save mode)
		this.newNameTextField = new Ext.form.TextField({
			fieldLabel : 'Name',
			disabled : false,
			anchor : '100%',
			allowBlank : false
		});

		// Form-Panel
		this.form = new Ext.form.FormPanel({
			frame : false,
			border : false,
			layout : 'border',
			window : this,
			monitorValid : true,
			bodyStyle : 'padding: 5px; background: none',
			items : [
					new Ext.Panel({
						monitorValid : true,
						layout : 'form',
						border : false,
						frame : false,
						region : 'south',
						autoHeight : true,
						bodyStyle : 'padding-top: 8px;  background: none',
						items : [this.newNameTextField ]
					}) ],

			buttons : [ {
				text : 'OK',
				id : 'ok-button',
				formBind : true,
				openDialog : true,
				window : this
			}, {
				text : 'Cancel',
				window : this,
				handler : this.onCancelClick
			} ]
		});


		Ext.apply(this, {

			width : 400,
			height : 300,
			title : "Change pwd",
			monitorValid : true,
			frame : true,
			layout : 'fit',
			modal : true,

			bodyStyle : 'padding: 8px',
			items : [ this.form ]

		});

		// call parent
		MapRed.utils.HdfsFileDialog.superclass.initComponent.apply(this,
				arguments);

	},

	// ok button listener


	// cancel button listener

	onCancelClick : function() {
		this.window.close();
	}

	// update textfield on selection

	

});
