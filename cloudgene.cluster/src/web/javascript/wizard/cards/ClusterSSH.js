Ext.ns('CloudgeneCluster.wizard');
var fileUpload=1;
CloudgeneCluster.wizard.ClusterSSH = Ext
		.extend(
				Ext.ux.Wiz.Card,
				{

					initComponent : function() {
							Ext
								.apply(
										this,
										{
											id: 'card4',
								        	wizRef: this,
								            title: 'Enter your credentials',
								            monitorValid: true,
								            onCardShow : this.loadSettings,
								            frame: true,
								            fileUpload: true,
								    		border: true,
								            layout: 'form',
											items: [
									
								            new Ext.form.RadioGroup({
								                fieldLabel: 'SSH Key Generation',
								                vertical: false,
								                id: "group2",
								                items: [{
								                    boxLabel: 'generate key',
								                    checked: true,
								                    name: 'key',
								                    inputValue: '1',
								                    listeners: {
								                        'check': function(checkbox, checked){
								                            if (checked) {
								                            	fileUpload=1;
								                            	Ext.getCmp('fFieldPub').setVisible(false);		
								                            	Ext.getCmp('fFieldPri').setVisible(false);	
								                            	Ext.getCmp('fFieldPri').setWidth(200);
								                            	Ext.getCmp('fFieldPub').setWidth(200);
								                            	Ext.getCmp('id-save-ssh').setValue(false);
								                            }
								                        }
								                    }
								                }, {
								                    boxLabel: 'upload key',
								                    name: 'key',
								                    inputValue: '2',
								                    listeners: {
								                        'check': function(checkbox, checked){
								                            if (checked) {
								                            	fileUpload=2;
								                            	 Ext.getCmp('fFieldPub').setVisible(true);		
								                            	 Ext.getCmp('fFieldPri').setVisible(true);	
								                            	 Ext.getCmp('fFieldPri').setWidth(200);
								                            	 Ext.getCmp('fFieldPub').setWidth(200);
								                            	 Ext.getCmp('id-save-ssh').setValue(false);
								                            }
								                        }
								                    }
								                },
								                {
								                    boxLabel: 'saved key',
								                    name: 'key',
								                    disabled: true,
								                    inputValue: '3',
								                    listeners: {
								                        'check': function(checkbox, checked){
								                            if (checked) {
								                            	fileUpload=1;
								                            	Ext.getCmp('fFieldPub').setVisible(false);		
								                            	Ext.getCmp('fFieldPri').setVisible(false);	
								                            	Ext.getCmp('fFieldPri').setWidth(200);
								                            	Ext.getCmp('fFieldPub').setWidth(200);
								                            	Ext.getCmp('id-save-ssh').setValue(false);
								                            }
								                        }
								                    }
								                }]}
								               ),
								                {  
									                xtype: 'checkbox',
									                fieldLabel: 'Save SSH Key', 
									                name: 'saveSsh',
									                id: 'id-save-ssh'
									            },
								                new Ext.form.FileUploadField(
														{
															id : 'fFieldPub',
															emptyText : 'SSH public key',
															fieldLabel : 'SSH public key',
															name : 'sampleFile',
															width : '100px',
															allowBlank : true,
															name : 'public'
																
														}),
														new Ext.form.FileUploadField(
																{
																	id : 'fFieldPri',
																	emptyText : 'SSH private key',
																	fieldLabel : 'SSH private key',
																	name : 'sampleFile',
																	width : '100px',
																	allowBlank : true,
																	name : 'private'
																		
																}),
																{
												            	id: "sshkey",
																border : false,
																bodyStyle : 'background:none;padding-bottom:30px;',
																html : 'If you need help, please have a look at our <a href=http://cloudgene.uibk.ac.at/documentation/aws_ssh.html target="_blank">SSH Key tutorial</a>'
															}]
										});

						CloudgeneCluster.wizard.ClusterSecurity.superclass.initComponent
								.apply(this, arguments);
					},
					loadSettings : function() {

						// general tool informations
						Ext.Ajax.request({
							url : '../getUserDetails',
							success : function(response) {

								arr = Ext.util.JSON.decode(response.responseText);

									if(arr.sshKey!=''){
										Ext.getCmp('fFieldPub').setVisible(false);		
		                            	Ext.getCmp('fFieldPri').setVisible(false);	
		                            	Ext.getCmp('fFieldPri').setWidth(200);
		                            	Ext.getCmp('fFieldPub').setWidth(200);
										Ext.getCmp('group2').items.items[2].setValue(true);
										Ext.getCmp('id-save-ssh').setValue(true);
										Ext.getCmp('group2').items.items[2].setDisabled(false);
										};
							}
						});

					}
				});