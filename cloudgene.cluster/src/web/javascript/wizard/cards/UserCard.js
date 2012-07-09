Ext.ns('CloudgeneCluster.wizard');

CloudgeneCluster.wizard.UserCard = Ext
		.extend(
				Ext.ux.Wiz.Card,
				{

					initComponent : function() {

						Ext
								.apply(
										this,
										{
											id: 'cardPwd',
								            
								            items: [
								            {
								            title : 'Add a new user',
											id : 'fieldset-target1',
											xtype : 'fieldset',
											autoHeight : true,
											defaults : {
												width : 150,
												labelStyle : 'font-size:12px'
											},								            
											defaultType : 'textfield',
											items : [new Ext.form.TextField({
								            	id: 'username',
								            	value : '',
												fieldLabel : '  Username',
												name : 'username',
												width : 150,
												allowBlank : true
								            }),
								            new Ext.form.TextField({
								            	id: 'pwd',
								            	value : '',
												fieldLabel : '  Password',
												name : 'pwd',
												width : 150,
												inputType : 'password',
												allowBlank : true
								            }),
								            new Ext.form.TextField({
								            	id: 'pwdControl',
								            	value : '',
												fieldLabel : '  Retype password',
												name : 'pwdControl',
												width : 150,
												inputType : 'password',
												allowBlank : true
								            }),
								            new Ext.form.RadioGroup({
								                fieldLabel: 'User type',
								                vertical: false,
								                id: "groupAdmin",
								                items: [{
								                    boxLabel: 'Standard',
								                    checked: true,
								                    name: 'type',
								                    inputValue: '1'
								                }, {
								                    boxLabel: 'Admin',
								                    name: 'type',
								                    inputValue: '2'
								                 
								                }]})
											]}
								            
								            ]
										});

						CloudgeneCluster.wizard.UserCard.superclass.initComponent
								.apply(this, arguments);
					}
				});