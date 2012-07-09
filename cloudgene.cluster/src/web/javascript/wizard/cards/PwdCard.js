Ext.ns('CloudgeneCluster.wizard');

CloudgeneCluster.wizard.PwdCard = Ext
		.extend(
				Ext.ux.Wiz.Card,
				{

					initComponent : function() {

						Ext
								.apply(
										this,
										{
											id: 'cardPwd',
								            title: 'Pwd',
								            
								            items: [
								            new Ext.form.TextField({
								            	id: 'pwdOld',
								            	value : '',
												fieldLabel : '  Current password',
												name : 'pwdOld',
												width : 150,
												inputType : 'password',
												allowBlank : true
								            }),
								            {
								            title : 'Password update',
											id : 'fieldset-target1',
											xtype : 'fieldset',
											autoHeight : true,
											defaults : {
												width : 150,
												labelStyle : 'font-size:12px'
											},								            
											defaultType : 'textfield',
											items : [new Ext.form.TextField({
								            	id: 'pwdNew',
								            	value : '',
												fieldLabel : '  New password',
												name : 'pwdNew',
												width : 150,
												inputType : 'password',
												allowBlank : true
								            }),
								            new Ext.form.TextField({
								            	id: 'pwdControl',
								            	value : '',
												fieldLabel : '  Retype new password',
												name : 'pwdControl',
												width : 150,
												inputType : 'password',
												allowBlank : true
								            })]}
								            ]
										});

						CloudgeneCluster.wizard.PwdCard.superclass.initComponent
								.apply(this, arguments);
					}
				});