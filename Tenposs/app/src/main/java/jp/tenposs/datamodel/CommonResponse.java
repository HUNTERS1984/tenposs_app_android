package jp.tenposs.datamodel;

import java.io.Serializable;

/**
 * Created by ambient on 7/25/16.
 */
public class CommonResponse implements Serializable {

    public final static int ResultSuccess = 1000;//	OK
    public final static int ResultErrorNoCharacterRegisted = 9993;//		No character is registed on server
    public final static int ResultErrorPoinyNotEnough = 9994;//		Point is not enought
    public final static int ResultErrorUserNotValidated = 9995;//		User is not validated.
    public final static int ResultErrorUserNotExisted = 9996;//		User existed.
    public final static int ResultErrorMethodNotValid = 9997;//		Method is not valid.
    public final static int ResultErrorInvalidToken = 9998;//		invalid token
    public final static int ResultErrorException = 9999;//		Lỗi exception
    public final static int ResultErrorDBConnection = 1001;//		Lõi mất kết nối DB/hoac thuc thi cau SQL
    public final static int ResultErrorMissingParameters = 1002;//		Số lượng Paramater không đầy đủ
    public final static int ResultErrorInvalidParameters = 1003;//		Kieu Parameter không đúng đắn.
    public final static int ResultErrorInvalidValue = 1004;//		Value cua parameter khong hop le
    public final static int ResultErrorUnknown = 1005;//		Unknown error

    public final static int ResultErrorEmailNotCorrect = 1006;   //Email not correct
    public final static int ResultErrorAddressDoesNotExists = 1007;   //Address does not exits
    public final static int ResultErrorCannotSendEmail = 1008;   //Can not send email
    public final static int ResultErrorAccountIsNotActive = 1009;   //Account is not active
    //    public final static int ResultError = 1010;   //Create Room Success
    public final static int ResultErrorTimeExpired = 1011;   //Time expire
    public final static int ResultErrorSigNotExist = 1012;   //Parameter sig not exist
    public final static int ResultErrorSigNotValid = 1013;   //Parameter sig is not valid
    public int code;
    public String message;
    //public Object data;
}
