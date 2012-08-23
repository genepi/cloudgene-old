Ext.onReady(function() {
	Ext.QuickTips.init();

	// Create a variable to hold our EXT Form Panel.
		// Assign various config options as seen.
		var panel = new Ext.Panel( {
			layout : 'border',
			defaults : {
				collapsible : true,
				split : false,
				bodyStyle : 'padding:15px'
			},
			items : [ 
			    Ext.getCmp('toolbar'),
			         {
				xtype : 'clusterQueue',
				title : 'Running Clusters',
				collapsible : false,
				region : 'center',
				margins : '5 0 0 0'
			},
			{
				xtype : 'box',
				cls : 'footer',
				region : 'south',
				html : '<div class="footer" id="footer"> Cloudgene is free software and has been created by <a href="mailto:lukas.forer@i-med.ac.at">Lukas Forer</a> and <a href="mailto:sebastian.schoenherr@uibk.ac.at">Sebastian Schoenherr</a>.  See our <a target="blank" href="http://cloudgene.uibk.ac.at">website</a> for further details. <a href="http://dbis-informatik.uibk.ac.at/" title="Database and Information Systems - Institute of Computer Science - University of Innsbruck" target="_blank"><img src="images/dbis.png" align="right" hspace="10" border="2"></a>  <a href="http://www.i-med.ac.at/genepi/" title="Division of Genetic Epidemiology" target="_blank"><img src="images/genepi.png" align="right" border="2" hspace="10" alt="GENEPI"> </a> </div>',
				height : 59,
				border:false
			}		
			]

		});

		var viewport = new Ext.Viewport( {
			layout : 'border',
			id : 'viewport',
			items : [ {
				xtype : 'box',
				region : 'north',
				applyTo : 'header',
				height : 55
			}, {
				region : 'center',
				layout : 'fit',
				items : [ panel ]
			} ]
		});

	});