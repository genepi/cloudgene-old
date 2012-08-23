Ext.ns('CloudgeneCluster.wizard');
function sendData() {
	//set slider to textfield, since its a hidden field
	Ext.getCmp("amount").setValue(Ext.getCmp("amount2").getValue());
	 var values = {};
	 var formValues = {}; 
	 var instance = this;
	 	for (var i = 0, len = this.cards.length; i < len; i++) {
	        formValues = this.cards[i].form.getValues(false);
	        for (var a in formValues) {
	            values[a] = formValues[a];
	        }
	    }
	 
	if (fileUpload == 1) {
	this.showLoadMask(true, 'validating');
    this.switchDialogState(false);
    Ext.Ajax.request({
        url: '../createCluster',
        
        // params: values,
        method: 'POST',
        scope: this,
        jsonData: values,
        
        failure: function(response, request){
            this.switchDialogState(true);
            Ext.Msg.show({
                title: 'Exception',
                msg: response.responseText,
                buttons: Ext.Msg.OKCANCEL,
                fn: function(btn, text){
                   
                }
            });
        },
        success: function(response, request){
            this.switchDialogState(true);
            this.close();
            refreshJobs();
        }
    });
}
else {
    this.showLoadMask(true, 'validating');
    this.switchDialogState(false);
    var form = this.cards[3].getForm();
    if (form.isValid()) {
    form.submit({
            url: '../keyUpload',
            waitTitle: 'File Upload',
            fileUpload: true,
            disableCaching: true,
            
            params: {
            	amount: values.amount,
            	cluster: values.cluster,
                loginPassword: values.loginPassword,
                loginUsername: values.loginUsername,
                name: values.name,
                saveCre: values.saveCre,
                saveSsh: values.saveSsh,
                s3Export: values.s3Export,
                bucketName: values.bucketName,
                type: values.type,
                program: values.program
            },
            waitMsg: 'Uploading file...',
            success: function(response, request){
            	instance.switchDialogState(true);
            	instance.close();
                refreshJobs();
            },
            failure : function(form, v) {
				var serverResponse = JSON.parse(v.response.responseText);
				instance.switchDialogState(true);
								Ext.Msg.show({
						title : 'Error',
						msg : serverResponse.message,
						modal : false,
						icon : Ext.Msg.ERROR,
						buttons : Ext.Msg.OK
					});
				
			}
        });
    }
    
}
}

function sendDataPwd() {
	//set slider to textfield, since its a hidden field
	 var values = {};
	 var formValues = {}; 
	 var instance = this;
	        formValues = this.cards[0].form.getValues(false);
	        for (var a in formValues) {
	            values[a] = formValues[a];
	        }
	this.showLoadMask(true, 'validating');
    this.switchDialogState(false);
    
    Ext.Ajax.request({
        url: '../changePwd',
        
        // params: values,
        method: 'POST',
        scope: this,
        jsonData: values,
        
        failure: function(response, request){
            this.switchDialogState(true);
            Ext.Msg.show({
                title: 'Exception',
                msg: response.responseText,
                buttons: Ext.Msg.OKCANCEL,
                fn: function(btn, text){
                   
                }
            });
        },
        success: function(response, request){
            this.switchDialogState(true);
            this.close();
            refreshJobs();
        }
    });

}

function sendDataUser() {
	//set slider to textfield, since its a hidden field
	 var values = {};
	 var formValues = {}; 
	 var instance = this;
	        formValues = this.cards[0].form.getValues(false);
	        for (var a in formValues) {
	            values[a] = formValues[a];
	        }
	this.showLoadMask(true, 'validating');
    this.switchDialogState(false);
    
    Ext.Ajax.request({
        url: '../addUser',
        
        // params: values,
        method: 'POST',
        scope: this,
        jsonData: values,
        
        failure: function(response, request){
            this.switchDialogState(true);
            Ext.Msg.show({
                title: 'Exception',
                msg: response.responseText,
                buttons: Ext.Msg.OKCANCEL,
                fn: function(btn, text){
                   
                }
            });
        },
        success: function(response, request){
            this.switchDialogState(true);
            this.close();
            refreshJobs();
        }
    });

}