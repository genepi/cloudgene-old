Ext.ns('MapRed.wizards');

MapRed.wizards.SelectSftpServerCard = Ext.extend(Ext.ux.Wiz.Card, {

	serverField : null,

	userField : null,

	passwordField : null,

	sftpModeBox : null,

    folder: "",

	initComponent : function() {

		this.serverField = new Ext.form.TextField({
			id : "server",
			name : 'server',
			value : 'sftp://',
			fieldLabel : 'SFTP-Server',
			allowBlank : false
		});

		this.userField = new Ext.form.TextField({
			id : "username",
			name : 'username',
			fieldLabel : 'Username',
			value : '',
			allowBlank : true
		});

		this.passwordField = new Ext.form.TextField({
			id : "password",
			name : 'password',
			fieldLabel : 'Password',
			allowBlank : true,
			inputType : 'password',
			value : ''
		});

		this.sftpModeBox = new Ext.form.RadioGroup({
			fieldLabel : 'Mode',
			vertical : false,
			id : "group1",
			items : [ {
				boxLabel : 'Anonymous',
				name : 'buttonMode',
				inputValue : '2',
				checked : true,
				userField : this.userField,
				passwordField : this.passwordField,
				listeners : {
					'check' : function(checkbox, checked) {
						if (checked) {
							this.userField.setVisible(false);
							this.passwordField.setVisible(false);
						}
					}
				}
			}, {
				boxLabel : 'Standard Login',
				name : 'buttonMode',
				inputValue : '1',
				userField : this.userField,
				passwordField : this.passwordField,
				listeners : {
					'check' : function(checkbox, checked) {
						if (checked) {
							this.userField.setVisible(true);
							this.passwordField.setVisible(true);
						}
					}
				}
			} ]

		});

		Ext.apply(this, {
			id : 'card2',
			wizRef : this,
			title : 'Import from SFTP-Server.',
			monitorValid : true,
			frame : false,
			fileUpload : true,
			border : false,
			height : '100%',
			folder: this.folder,
			defaults : {
				labelStyle : 'font-size:11px'
			},
			items : [
					{
						border : false,
						bodyStyle : 'background:none;padding-bottom:30px;',
						html : 'Please specify the SFTP/SSH connection.'
					},
					{
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
					},
					{
						title : 'SFTP-Server',
						id : 'fieldset-amazon',
						xtype : 'fieldset',
						autoHeight : true,
						defaults : {
							width : 210,
							labelStyle : 'font-size:11px'
						},
						defaultType : 'textfield',
						items : [ this.sftpModeBox, this.serverField,
								this.userField, this.passwordField ]
					} ]
		});

		// call parent
		MapRed.wizards.ImportDataSftpCard.superclass.initComponent.apply(this,
				arguments);

	},

	// sftp-fields

	setAnonymous : function() {
		this.userField.setVisible(false);
		this.passwordField.setVisible(false);
		this.userField.setValue('');
		this.passwordField.setValue('');
	},

	setStandardLogin : function() {
		this.userField.setVisible(true);
		this.passwordField.setVisible(true);
	}
});
