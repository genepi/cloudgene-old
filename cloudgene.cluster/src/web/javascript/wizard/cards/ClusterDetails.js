Ext.ns('CloudgeneCluster.wizard');

CloudgeneCluster.wizard.ClusterDetails = Ext
		.extend(
				Ext.ux.Wiz.Card,
				{

					initComponent : function() {
						Ext.form.TextfieldSlider = Ext.extend(Ext.Slider, {
							  isFormField: true,
							  initComponent: function() {
							    this.originalValue = this.value;
							    Ext.form.TextfieldSlider.superclass.initComponent.call(this);
							  },
							  onRender: function(){
							    Ext.form.TextfieldSlider.superclass.onRender.apply(this, arguments);
							    Ext.DomHelper.insertAfter(this.el,{
							        tag: 'div',
							        id: this.id +'_slidertextdiv',
							        style: 'position: relative; float:right;width:60px;height:20px;margin-top:-22px;'
							    });
							    this.sliderField = new Ext.form.NumberField({
							        renderTo:this.id +'_slidertextdiv',
							        id: this.id +'_slidertext',
							        name: this.name +'_slidertext',
							        value: this.value,
							        enableKeyEvents:true,
							        width:60,
							        minValue:this.minValue,
							        maxValue:this.maxValue ,
							        scope:this,
							        listeners: {
							            keyup : function() {
							                this.adjustValue.defer(500, this);
							            },
							            scope:this
							        }
							    });
							  },
							  adjustValue : function(){
							    this.setValue(this.sliderField.getValue());
							    if(this.sliderField.getValue()==""){
							         this.setValue(this.sliderField.minValue);
							    }
							    this.sliderField.clearInvalid();
							  },
							  setValue: function(v) {
							    v = parseFloat(v);
							    if(this.maxValue && v > this.maxValue) v = this.maxValue;
							    if(this.minValue && v < this.minValue) v = this.minValue;
							    Ext.form.TextfieldSlider.superclass.setValue.apply(this, [v]);
							   
							    if(this.rendered){
							      if(v<=0){
							        Ext.getDom(this.id +'_slidertext').value=0;
							      } else {
							        Ext.getDom(this.id +'_slidertext').value=v;
							      }
							      
							    }
							  },
							  reset: function() {
							    this.setValue(this.originalValue);
							    this.clearInvalid();
							  },
							  getName: function() {
							    return this.name;
							  },
							  validate: function() {
							    return true;
							  },
							  setMinValue : function(minValue){
							    this.minValue = minValue;
							    this.sliderField.minValue =  minValue;
							    return minValue;
							  },
							  setMaxValue : function(maxValue){
							    this.maxValue = maxValue;
							    this.sliderField.maxValue =  maxValue;
							    return maxValue
							  },
							  markInvalid: Ext.emptyFn,
							  clearInvalid: Ext.emptyFn
							});
							Ext.reg('textfieldslider', Ext.form.TextfieldSlider);
						Ext
								.apply(
										this,
										{
											id : 'card2',
											wizRef : this,
								            title: 'Enter your cluster details',
											monitorValid : true,
											border : false,
											items: [
								                    new Ext.form.RadioGroup({
								                fieldLabel: 'Data Source',
								                vertical: false,
								                id: "group",
								                items: [{
								                    boxLabel: 'Amazon EC2',
								                    checked: true,
								                    name: 'cluster',
								                    inputValue: 'aws-ec2',
								                    listeners: {
								                        'check': function(checkbox, checked){
								                            if (checked) {
								                            	Ext.getCmp('rackspace').setVisible(false);
								                            	Ext.getCmp('name').setVisible(true);
								                            	Ext.getCmp('amount').setVisible(true);
								                            	Ext.getCmp('amount2').setVisible(false);
								                            	Ext.getCmp('prog').setVisible(true);
								                            }
								                        }
								                    }
								                }/*, {
								                    boxLabel: 'Rackspace',
								                    name: 'cluster',
								                    inputValue: '3',
								                    listeners: {
								                        'check': function(checkbox, checked){
								                            if (checked) {
								                            	Ext.getCmp('rackspace').setVisible(true);
								                            	Ext.getCmp('name').setVisible(false);
								                            	Ext.getCmp('amount').setVisible(false);
								                            	Ext.getCmp('amount2').setVisible(false);
								                            	Ext.getCmp('prog').setVisible(false);
								                            }
								                        }
								                    }
								                }*/]}),
								                {
								                	
								                	id: 'rackspace',
								                
								                    border: false,
								                    
								                    hidden: true,
								                    
								                    bodyStyle: 'background:none;',
								                    
								                    html: 'Cluster Setup for Rackspace is currently under development and available end of July 2011'
								                
								                },
												{
													title : 'Cluster Details',
													id : 'fieldset-target',
													xtype : 'fieldset',
													autoHeight : true,
													defaults : {
														width : 210,
														labelStyle : 'font-size:11px'
													},
													defaultType : 'textfield',
													items : [ new Ext.form.TextField({
										            	id: 'name',
										            	value: '',
														fieldLabel: '  Cluster name',
														name: 'name',
														allowBlank : false
										            }),
										            new Ext.form.TextField({
										            	id: 'amount',
										            	value: '',
														fieldLabel: '  Amount of instances',
														name: 'amount',
														hidden:true,
														allowBlank : true
										            }),
										            new Ext.form.ComboBox({
										            	id: 'prog',
										                fieldLabel: 'User Program',
										                name: 'program',
										                store: new Ext.data.Store({
										           		 url: '../getPrograms',
										           		 autoLoad:false, 
										        		 reader: new Ext.data.JsonReader({
										        			 id: 'reader',
										        			 totalProperty: "results",
										        		        fields: [{
										        		            name: 'name',
										        		            type: 'string'
										        		        },
										        		        {
										        		            name: 'version',
										        		            type: 'string'
										        		        },
										        		        {
										        		            name: 'description',
										        		            type: 'string'
										        		        },
										        		        {
										        		            name: 'website',
										        		            type: 'string'
										        		        }]        
										        		    })}),
										                displayField: 'name',
										                typeAhead: true,
										                width: 130,
										                allowBlank: false,
										                editable: false,
										                mode: 'local',
										                triggerAction: 'all',
										                emptyText: 'Choose a program',
										                selectOnFocus: true,
										                listeners:{select:{fn:function(combo, record, value) {
										                    Ext.getCmp('descr').update("<p><b>" + record.json.name + " " + record.json.version +"</b></p><p>"+ record.json.description
										                    		+"</p>"+"<p><a href="+ record.json.website+" target= _blank>"+ record.json.website+"</a></p>");
										    			    Ext.getCmp('descr').show();
										    			    Ext.getCmp('type').store.load({ params : {
								                	            'prog' : Ext.getCmp('prog').value}});
										    			   
										                }
									                        }
}
										            }),
										            new Ext.form.ComboBox({
										            	id: 'type',
										                fieldLabel: 'Instance Type',
										                name: 'type',
										                store: new Ext.data.Store({
										           		 url: '../getTypes',
										           		 autoLoad:false, 
										           		 editable: false,
										        		 reader: new Ext.data.JsonReader({
										        			 id: 'reader',
										        			 totalProperty: "results",
										        		        fields: [{
										        		            name: 'value',
										        		            type: 'string'
										        		        }]        
										        		    })}),
										                displayField: 'value',
										                typeAhead: true,
										                width: 130,
										                allowBlank: false,
										                mode: 'local',
										                triggerAction: 'all',
										                emptyText: 'Choose a type',
										                selectOnFocus: true
										            }),
										            new Ext.Slider({
										            	id: 'amount2',
										            	value : 1,
														fieldLabel : ' Amount of nodes',
														name : 'amount',
										                width: 214,
										                increment: 1,
										                minValue: 0,
										                maxValue: 20,
										                plugins: new Ext.ux.SliderTip()
										            }) ]
												},
												  new Ext.Panel({
														id : "descr",
														name : 'descr',
														value : '',
														hideLabel : true,
														allowBlank : true,
														readOnly : true,
														anchor : '100%',
														bodyStyle : 'padding: 5px;',
														hidden : true
													    })
								           ]
										});
						
						CloudgeneCluster.wizard.ClusterDetails.superclass.initComponent
								.apply(this, arguments);
					}
				});