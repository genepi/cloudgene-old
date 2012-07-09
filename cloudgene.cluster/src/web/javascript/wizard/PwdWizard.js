Ext.ns('CloudgeneCluster.PwdWizard');

CloudgeneCluster.wizard.PwdWizard = Ext.extend(Ext.ux.Wiz, {
	initComponent : function() {

		Ext.apply(this, {
			 id: 'pwdWiz',
		        title: 'Password management',
		        headerConfig: {
		            title: 'Change password',
		            image : '../images/attention.png'
		          		        },
		        loadMaskConfig: {
		            'default': 'Please wait, validating input...'
		        },
		        cardPanelConfig: {
		            defaults: {
		                baseCls: 'x-small-editor',
		                bodyStyle: 'padding:40px 15px 5px 120px;background-color:#F6F6F6;',
		                border: false
		            }
		        },
		        height : 350,
		        width:500,
			 cards: [new CloudgeneCluster.wizard.PwdCard()],

		       listeners: {
		        	finish: sendDataPwd
		        }
		});

		CloudgeneCluster.wizard.PwdWizard.superclass.initComponent
				.apply(this, arguments);

	}

});
