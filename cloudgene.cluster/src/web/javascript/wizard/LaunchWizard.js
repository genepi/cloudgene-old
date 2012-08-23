Ext.ns('CloudgeneCluster.wizard');

CloudgeneCluster.wizard.LaunchWizard = Ext.extend(Ext.ux.Wiz, {
	initComponent : function() {

		Ext.apply(this, {
			 id: 'wizard',
		        title: 'Create a new cluster',
		        headerConfig: {
		            title: 'Welcome to Cloudgene',
		            image : '../images/cluster.png'
		        },
		        loadMaskConfig: {
		            'default': 'Please wait, validating input...'
		        },
		        cardPanelConfig: {
		            defaults: {
		                baseCls: 'x-small-editor',
		                bodyStyle : 'padding:20px;background-color:#ffffff;',
		                border: false
		            }
		        },
		        height : 430,
			 cards: [ new CloudgeneCluster.wizard.ClusterEntry(),
		        new CloudgeneCluster.wizard.ClusterDetails(),
		        new CloudgeneCluster.wizard.ClusterSecurity(),
		        new CloudgeneCluster.wizard.ClusterSSH()
		       ],

		       listeners: {
		        	finish: sendData
		        }
		});

		CloudgeneCluster.wizard.LaunchWizard.superclass.initComponent
				.apply(this, arguments);
	 fileUpload=1;
	 Ext.getCmp('fFieldPub').setVisible(false);		
   	 Ext.getCmp('fFieldPri').setVisible(false);	
	}

});
