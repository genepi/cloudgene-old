Ext.ns('CloudgeneCluster.UserWizard');

CloudgeneCluster.wizard.UserWizard = Ext.extend(Ext.ux.Wiz, {
	initComponent : function() {

		Ext.apply(this, {
			 id: 'addUser',
		        title: 'User management',
		        headerConfig: {
		            title: 'Add user',
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
		        height : 380,
		        width:500,
			 cards: [new CloudgeneCluster.wizard.UserCard()],

		       listeners: {
		        	finish: sendDataUser
		        }
		});

		CloudgeneCluster.wizard.UserWizard.superclass.initComponent
				.apply(this, arguments);

	}

});
