package jp.tenposs.datamodel;

import android.content.Context;

import java.io.Serializable;

import jp.tenposs.staffapp.R;

/**
 * Created by ambient on 7/25/16.
 */
public class CommonResponse implements Serializable {

    public final static int ResultSuccess = 1000;//	OK
    public final static int ResultErrorNoCharacterRegisted = 9993;//		No character is registed on server
    public final static int ResultErrorPointNotEnough = 9994;//		Point is not enought
    public final static int ResultErrorUserNotValidated = 9995;//		User is not validated.
    public final static int ResultErrorUserAlreadyExist = 9996;//		User existed.
    public final static int ResultErrorMethodNotValid = 9997;//		Method is not valid.
    public final static int ResultErrorInvalidToken = 9998;//		invalid token
    public final static int ResultErrorException = 9999;//		Lỗi exception
    public final static int ResultErrorDB = 1001;//		Lõi mất kết nối DB/hoac thuc thi cau SQL
    public final static int ResultErrorMissingParameters = 1002;//		Số lượng Paramater không đầy đủ
    public final static int ResultErrorInvalidParametersType = 1003;//		Kieu Parameter không đúng đắn.
    public final static int ResultErrorInvalidParametersValue = 1004;//		Value cua parameter khong hop le
    public final static int ResultErrorUnknown = 1005;//		Unknown error

    public final static int ResultErrorEmailNotCorrect = 1006;   //Email not correct
    public final static int ResultErrorAddressDoesNotExists = 1007;   //Address does not exits
    public final static int ResultErrorCannotSendEmail = 1008;   //Can not send email
    public final static int ResultErrorAccountIsNotActive = 1009;   //Account is not active
    public final static int ResultErrorTimeExpired = 1011;   //Time expire
    public final static int ResultErrorSignatureMissing = 1012;   //Parameter sig not exist
    public final static int ResultErrorSignatureInvalid = 1013;   //Parameter sig is not valid

    public final static int ResultErrorEmailNotActive = 99950;//		Email have not activated
    public final static int ResultErrorActiveCodeNotExist = 99951;//		Actvie code not exist
    public final static int ResultErrorActiveCodeExpire = 99952;//		Active code expire
    public final static int ResultErrorUserNotExist = 99953;//		User not exist
    public final static int ResultErrorRefreshTokenInvalid = 99954;//		Refresh token invalid
    public final static int ResultErrorAuthorizationInvalid = 99955;//		Auth invalid
    public final static int ResultErrorPasswordInvalid = 99956;//		Password not math
    public final static int ResultErrorMedthodInvalid = 99957;//		Method not allow
    public final static int ResultErrorTokenExpire = 10011;//                      Token has expired


    public int code;
    public String message;
    //public Object data;

    public static String getErrorMessage(Context context, int errorCode) {
        switch (errorCode) {
            case ResultErrorNoCharacterRegisted: {
                return context.getString(R.string.msg_error_not_registered_on_server);
            }
            case ResultErrorPointNotEnough: {
                return context.getString(R.string.msg_error_point_is_not_enough);
            }
            case ResultErrorUserNotValidated: {
                return context.getString(R.string.msg_error_user_is_not_validated);
            }
            case ResultErrorUserAlreadyExist: {
                return context.getString(R.string.msg_error_user_already_exist);
            }
            case ResultErrorMethodNotValid: {
                return context.getString(R.string.msg_error_method_is_not_valid);
            }
            case ResultErrorInvalidToken: {
                return context.getString(R.string.msg_error_invalid_token);
            }
            case ResultErrorException: {
                return context.getString(R.string.msg_error_exception);
            }
            case ResultErrorDB: {
                return context.getString(R.string.msg_error_db);
            }
            case ResultErrorMissingParameters: {
                return context.getString(R.string.msg_error_parameters_count);
            }
            case ResultErrorInvalidParametersType: {
                return context.getString(R.string.msg_error_parameters_type);
            }
            case ResultErrorInvalidParametersValue: {
                return context.getString(R.string.msg_error_parameter_value);
            }
            case ResultErrorUnknown: {
                return context.getString(R.string.msg_error_unknown);
            }
            case ResultErrorEmailNotCorrect: {
                return context.getString(R.string.msg_error_email_is_not_correct);
            }
            case ResultErrorAddressDoesNotExists: {
                return context.getString(R.string.msg_error_address_is_not_exist);
            }
            case ResultErrorCannotSendEmail: {
                return context.getString(R.string.msg_error_cannot_send_email);
            }
            case ResultErrorAccountIsNotActive: {
                return context.getString(R.string.msg_error_account_is_not_active);
            }
            case ResultErrorTimeExpired: {
                return context.getString(R.string.msg_error_time_expire);
            }
            case ResultErrorSignatureMissing: {
                return context.getString(R.string.msg_error_signature_missing);
            }
            case ResultErrorSignatureInvalid: {
                return context.getString(R.string.msg_error_signature_invalid);
            }
            default:
                return "";
        }
    }
}