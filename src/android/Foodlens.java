package cordova.plugins.foodlens;

import android.content.Intent;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import com.doinglab.foodlens.sdk.FoodLens;
import com.doinglab.foodlens.sdk.UIService;
import com.doinglab.foodlens.sdk.UIServiceResultHandler;
import com.doinglab.foodlens.sdk.errors.BaseError;
import com.doinglab.foodlens.sdk.network.model.UserSelectedResult;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * This class echoes a string called from JavaScript.
 */
public class Foodlens extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("launchFoodlensUI")) {
            this.launchFoodlensUI(callbackContext);
            return true;
        }

        return false;
    }

    private UIService uis;
    private void launchFoodlensUI(CallbackContext callbackContext){
        this.cordova.setActivityResultCallback (this);
        uis = FoodLens.createUIService(this.cordova.getActivity().getApplicationContext());
        uis.startFoodLensCamera(this.cordova.getActivity(), new UIServiceResultHandler() {
            @Override
            public void onSuccess(UserSelectedResult result) {
                try{
                    callbackContext.success(new JSONObject(result.toJSONString()));
                }catch(JSONException e){
                    Log.e("FoodlensPlugin", e.toString());
                    callbackContext.error(e.toString());
                }
            }

            @Override
            public void onCancel() {
                //Log.d("MSG_LOG", "Recognition Cancel");
                callbackContext.error("Cancel");
            }

            @Override
            public void onError(BaseError error) {
                //Log.d("MSG_LOG", error.getMessage());
                callbackContext.error(error.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) { //65281 for foodlens

        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode==65281)
            this.uis.onActivityResult(requestCode, resultCode, intent);
    }
}
