package com.example.foodrecipes.requests.responses;

import java.io.IOException;

import retrofit2.Response;


/**
 * Generic class for handling responses from Retrofit
 * @param <T>
 */
public class ApiResponse<T> {
    public ApiResponse<T> create(Throwable e) {
        return new ApiErrorResponse<>(!e.getMessage().equals("") ? e.getMessage()
                : "Unknown error\n Check network connection");
    }

    public ApiResponse<T> create(Response<T> response) {
        if (response.isSuccessful()) {
            T body = response.body();
            if(body instanceof RecipeSearchResponse)
                if(!CheckRecipeApiKey.isRecipeApiKeyValid((RecipeSearchResponse)body)) {
                    String errorMSG = "Api Key Expired or Invalid";
                    return new ApiErrorResponse<>(errorMSG);
                }

            if(body instanceof RecipeResponse)
                if(!CheckRecipeApiKey.isRecipeApiKeyValid((RecipeResponse)body)) {
                    String errorMSG = "Api Key Expired or Invalid";
                    return new ApiErrorResponse<>(errorMSG);
                }

            if (body.equals("") || response.code() == 204)//204 no content
                return new ApiEmptyResponse<>();

            else return new ApiSuccessResponse<>(body);
        } else {
            String errorMSG = "";
            try {
                errorMSG = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
                errorMSG = response.message();
            }
            return new ApiErrorResponse<>(errorMSG);
        }
    }


    /**
     * Generic success response from api
     * @param <T>
     */
    public class ApiSuccessResponse<T> extends ApiResponse<T> {
        private T body;

        ApiSuccessResponse(T body) {
            this.body = body;
        }

        public T getBody() {
            return body;
        }
    }


    /**
     * Generic Error response from API
     *@param <T>
     */
    public class ApiErrorResponse<T> extends ApiResponse<T> {
        private String errorMSG;

        public ApiErrorResponse(String errorMSG) {
            this.errorMSG = errorMSG;
        }

        public String getErrorMessage() {
            return errorMSG;
        }
    }


    /**
     * separate class for HTTP 204 resposes so that we can make ApiSuccessResponse's body non-null.
     */
    public class ApiEmptyResponse<T> extends ApiResponse<T> {
    }//Gonna Return Nothing bcoz if the
    // response wes 200(Successful but the server return nothing then we should handel this case for good practise)
}
