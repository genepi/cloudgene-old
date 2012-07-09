Ext.ns('CloudgeneCluster.wizard');
CloudgeneCluster.wizard.ClusterSecurity = Ext
		.extend(
				Ext.ux.Wiz.Card,
				{

					initComponent : function() {
							Ext
								.apply(
										this,
										{
											id: 'card3',
								        	wizRef: this,
								            title: 'Enter your credentials',
								            monitorValid: true,
								            onCardShow : this.loadSettings,
								            frame: true,
								    		border: true,
								            layout: 'form',
											items: [
											{
												title : 'Security Credentials',
												id : 'fieldset-target1',
												xtype : 'fieldset',
												autoHeight : true,
												defaults : {
													width : 210,
													labelStyle : 'font-size:11px'
												},
												defaultType : 'textfield',
												items : [new Ext.form.TextField({
									        	  	id: 'username',
									            	value : '',
													fieldLabel : '  Access Key ID',
													name : 'loginUsername',
													width : 260,
													allowBlank : false,
													listeners: {
													    'render': function(c) {
													      c.getEl().on('keyup', function() {
													    	  Ext.getCmp('bucket-name-private').reset();
													    	  Ext.getCmp('bucket-name-private').store.removeAll();
													    	  Ext.getCmp('id-s3').setValue(false);
													      }, c);
													    }
													  }
									            }),
									            new Ext.form.TextField({
									            	id: 'pwd',
									            	value : '',
													fieldLabel : '  Secret Access Key',
													name : 'loginPassword',
													width : 260,
													inputType : 'password',
													allowBlank : false,
													listeners: {
													    'change': function(){
													    	Ext.getCmp('id-s3').setValue(false);
													    }}
									            }),
									            {  
									                xtype: 'checkbox',
									                fieldLabel: 'Save credentials?', 
									                name: 'saveCre',
									                id: 'id-save',
									                listeners : {
									                	  'check' : {
									                	    fn : function(checkbox, checked){
									                	    	if(checked&&Ext.getCmp('username').getValue()!=''){
									                	    	Ext.Ajax.request({
									    							url : '../checkKey',
									    							method: 'POST',
									    							params : {
										                	            'usr' : Ext.getCmp('username').getValue(),'pwd' : Ext.getCmp('pwd').getValue()
										                	        },
									    							failure: function(response, request){
									    					            Ext.Msg.show({
									    					                title: 'Exception',
									    					                msg: response.responseText,
									    					                buttons: Ext.Msg.OKCANCEL,
									    					                fn: function(btn, text){
									    					                   
									    					                }
									    					            });
									    					            Ext.getCmp('id-save').setValue(false);
									    					        }
									    						});
									                	  }
									                	    }
									                }
									            }
									            }]
											},
											{  
								                xtype: 'checkbox',
								                fieldLabel: 'Export results to S3', 
								                name: 's3Export',
								                id: 'id-s3',
								                listeners : {
								                	  'check' : {
								                	    fn : function(checkbox, checked){
								                	    	if(Ext.getCmp('pwd').getValue()!=""&&Ext.getCmp('username').getValue()!=""){
								                	    		if(checked){
								                	        Ext.getCmp('bucket-name-private').store.load({ params : {
								                	            'usr' : Ext.getCmp('username').getValue(),'pwd' : Ext.getCmp('pwd').getValue()
								                	        }});
								                	    	Ext.getCmp('bucket-name-private').setVisible(true);
								                	    	Ext.getCmp('bucket-name-private').setWidth(200);
								                	    	}
								                	    	else
								                	    	Ext.getCmp('bucket-name-private').setVisible(false);
									                	 }
								                	    	else{Ext.Msg.alert('Note', 'Please add your credentials first');
								                	    		this.setValue(false);}
								                	  }
								                }
								            }},
								    		new Ext.form.ComboBox({
												id : "bucket-name-private",
												name : 'bucketName',
												fieldLabel : 'S3 Bucket',
												store : new Ext.data.Store({
													 proxy: new Ext.data.HttpProxy(
															    {url: '../getMyBuckets',
															    method: 'POST',
																failure: function(response){
																	Ext.Msg.alert('Note', 'Please check your credentials!')
																Ext.getCmp('id-s3').setValue(false);
																}}
															  ),
													id : 'store',
													autoLoad : false,
													reader : new Ext.data.JsonReader({
														id : 'bucket-reader',
														fields : [ {
															name : 'text',
															type : 'string'
														}, {
															name : 'id',
															type : 'string'
														} ]
													})
												}),
												displayField : 'text',
												allowBlank : true,
												editable : false,
												hidden : true,
												typeAhead : true,
												mode : 'local',
												triggerAction : 'all',
												emptyText : 'Choose a bucket',
												selectOnFocus : true
											}),
																{
												            	id: "sshkey2",
																border : false,
																bodyStyle : 'background:none;padding-bottom:30px;',
																html : 'If you need help, please have a look at our <a href=http://cloudgene.uibk.ac.at/documentation/aws_security.html target="_blank">AWS Security tutorial</a>'
															}]
										});

						CloudgeneCluster.wizard.ClusterSecurity.superclass.initComponent
								.apply(this, arguments);
					},
										
					loadSettings : function() {

						// general tool informations
						Ext.Ajax.request({
							url : '../getCloudCredentials',
							success : function(response) {

								arr = Ext.util.JSON.decode(response.responseText);

									if(arr.cloudKey!=''){
										Ext.getCmp('username').setValue(arr.cloudKey);
										Ext.getCmp('pwd').setValue(arr.cloudSecure);
									}
									if(arr.saveKey==true){
										Ext.getCmp('id-save').setValue(true);
										}
;
							}
						});

						if (this.monitorValid) {
							this.startMonitoring();
						}

					}
				});