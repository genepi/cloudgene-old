		
		var toolbar = new Ext.Toolbar(
				{
						id: 'toolbar',
					    region: "north",
			            xtype: 'toolbar',
			            title: 'Add new Job',
			            height: 65,
			            collapsible: false,
			            split: false,
			            
			            items: [{
			                xtype: 'tbspacer'
			            },{
			                xtype: 'tbbutton',
			                icon: '../images/add.png',
			                iconAlign: 'top',
			                cls: 'x-btn-text-icon',
			                scale: 'large',
			                text: 'Create a Cluster',
			                tooltip: 'Creae a EC2 Cluster',
			                handler: function(btn){
			                   var wiz1= new CloudgeneCluster.wizard.LaunchWizard();
			                   wiz1.show();
			                }
			            },
			            {
			                xtype: 'tbseparator',
			                scale: 'medium'
			            }, {
			                xtype: 'tbbutton',
			                icon: '../images/minus.png',
			                iconAlign: 'top',
			                cls: 'x-btn-text-icon',
			                scale: 'large',
			                text: 'Destroy a Cluster',
			                tooltip: 'Destoy a EC2 Cluster',
			                
			                handler: function(btn){
			                	if(recordID!=null){
			                	Ext.Msg.confirm('Check!', 'Shutdown cluster?', function(btn, text){
			                	if (btn == 'yes'){
			                	 var values = {};
			                	 Ext.Ajax.request({
			            	        	url: '../checkKey',
		                                success: function(response){
		                                	values[0] = recordID;
	      			            	    	values[1] = '';
		                                	 Ext.Ajax.request({
		                                		    jsonData: values,
		      			            	        	url: '../destroyCluster',
		      		                                success: function(response){
		      		                                	refreshJobs();
		      		                                }
		      		                            });
		                                },
		                                failure : function(response, request) {
		                                	Ext.Msg.prompt('Security Prompt', 'Please enter your EC2 Secret Key.\n <br /> <br /> Export all your results before destroying the cluster.', function(btn, text){
		      			            	      if (btn === 'ok'){
		      			            	    	values[0] = recordID;
		      			            	    	values[1] = text;
		      			            	        Ext.Ajax.request({
		      			            	        	jsonData: values,
		      		                                url: '../destroyCluster',
		      		                                success: function(response){
		      		                                	
		                                  		  				refreshJobs();
		      		                                },
		      		                                failure : function(response, request) {
		      		                                	Ext.Msg.alert('Status', 'Please check your EC2 credentials');
		      		                        		}
		      		                            });
		      			            	      }
		      			            	    });
		                        		}
		                            });
			                		 }
			                		 });
			            }
			                }
			            },
						{
			                xtype: 'tbfill'
			            }, 
			            {
			                xtype: 'box',
			                autoEl: {tag: 'img', src:'../images/legende.png'}
			            },
			            {
							text : 'Config',
							tooltip : 'Configruation',
							xtype : 'tbbutton',
							icon : '../images/smart.png',
							iconAlign : 'top',
							cls : 'x-btn-text-icon',
							scale : 'large',

							menu : [
									{
										text : 'Change password...',
										handler : function(
												btn) {

											var dialog = new CloudgeneCluster.wizard.PwdWizard();
											dialog.show();

										}
									},
									{
										text : 'Add user...',
										handler : function(
												btn) {

											var dialog = new CloudgeneCluster.wizard.UserWizard();
											dialog.show();

										}
									}
									]

						/*
						 * handler : function(btn) {
						 * 
						 * var wizard = new
						 * MapRed.wizards.ImportData();
						 * wizard.show(); }
						 */
						},
			            {
			                xtype: 'tbbutton',
			                icon: '../images/logout_icon.png',
			                iconAlign: 'top',
			                cls: 'x-btn-text-icon',
			                scale: 'large',
			                text: 'Logout',
			                handler: function(btn){
			                    window.location = "logout";
			                }
			            }]		            
			         
			        });