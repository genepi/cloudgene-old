Ext.ns('CloudgeneCluster.wizard');

CloudgeneCluster.wizard.ClusterEntry = Ext
		.extend(
				Ext.ux.Wiz.Card,
				{

					initComponent : function() {

						Ext
								.apply(
										this,
										{
											id: 'card1',
								            title: 'Welcome',
								            onCardShow : this.loadSettings,
								            onCardHide : this.loadSettings1,
								            items: [{
								            	
								                border: false,
								                
								                bodyStyle: 'background:none;',
								                
								                html: 'Welcome to Cloudgene-Cluster! <br> Cloudgene supports you to launch cluster in the cloud. <br> <br> Please keep in mind ' +
								                
								                ' that starting a cluster costs money. Since Cloudgene is still under development, always check if clusters are shut down properly via the AWS Console or similar tools.'
								            
								            }]
										});

						CloudgeneCluster.wizard.ClusterEntry.superclass.initComponent
								.apply(this, arguments);
					},
				loadSettings : function() {

					// general tool informations
					Ext.Ajax.request({
						url : '../loadPrograms'
					});
					
					if (this.monitorValid) {
						this.startMonitoring();
					}

				},
				loadSettings1 : function() {

					// general tool informations
					Ext.getCmp('prog').store.reload();
					if (this.monitorValid) {
						this.startMonitoring();
					}

				}
				});