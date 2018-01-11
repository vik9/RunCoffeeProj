package toplab18.runcoffee.Network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Vik-Note on 11.01.2018.
 */

public interface RestService {

    @POST("login")
    Call<ModelUserRes> loginUser (@Body ModelLoginReq req);

}
