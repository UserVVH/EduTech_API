package com.edutechit.edutechit_api.util;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import org.springframework.web.client.RestTemplate;

@Component
public class DropboxUtils {

  //    @Value("${dropbox.access.token}")
//    private String accessToken;

  @Value("${dropbox.access.token}")
  private String tokenUrl;

  @Value("${aes.secret}")
  private String secretKeyAES;

  private String dropboxAccessToken;

  @PostConstruct
  public void init() {
    try {
      // Lấy nội dung từ tokenUrl
      RestTemplate restTemplate = new RestTemplate();
      String encryptedData = restTemplate.getForObject(tokenUrl, String.class);

      // Kiểm tra nếu nội dung trả về không null
      if (encryptedData != null) {
        encryptedData = encryptedData.trim(); // Loại bỏ khoảng trắng đầu/cuối
        System.out.println("Encrypted Data Retrieved: " + encryptedData);

        // Giải mã dữ liệu AES
        dropboxAccessToken = decryptAES(encryptedData, secretKeyAES); // Sử dụng secret key đã có
        System.out.println("Dropbox Access Token: " + dropboxAccessToken);
      } else {
        System.err.println("Error: No token retrieved from URL.");
      }
    } catch (Exception e) {
      System.err.println("Error during initialization: " + e.getMessage());
    }
  }

  // Phương thức giải mã AES với khóa secretKey đã có
  private String decryptAES(String encryptedData, String secretKey) throws Exception {
    // Chuyển đổi Base64 về dạng byte
    byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);

    // Kiểm tra chiều dài dữ liệu
    if (encryptedBytes.length < 16) {
      throw new IllegalArgumentException("Encrypted data is too short.");
    }

    // Tách IV (16 byte đầu tiên)
    byte[] iv = new byte[16];
    System.arraycopy(encryptedBytes, 0, iv, 0, iv.length);

    // Dữ liệu mã hóa (sau IV)
    byte[] encryptedDataWithoutIV = new byte[encryptedBytes.length - iv.length];
    System.arraycopy(encryptedBytes, iv.length, encryptedDataWithoutIV, 0, encryptedDataWithoutIV.length);

    // Tạo khóa AES từ secretKey (sử dụng SHA-256 để tạo khóa 32 byte nếu cần)
    SecretKeySpec keySpec = new SecretKeySpec(hashSecretKey(secretKey), "AES");

    // Tạo đối tượng Cipher để giải mã
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    IvParameterSpec ivSpec = new IvParameterSpec(iv); // IV phải dùng đúng như khi mã hóa
    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

    // Giải mã dữ liệu
    byte[] decryptedBytes = cipher.doFinal(encryptedDataWithoutIV);

    // Chuyển dữ liệu giải mã thành chuỗi
    return new String(decryptedBytes, StandardCharsets.UTF_8);
  }

  // Phương thức băm secretKey để tạo khóa 32 byte (SHA-256)
  private byte[] hashSecretKey(String secretKey) throws Exception {
    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
    return sha256.digest(secretKey.getBytes(StandardCharsets.UTF_8));
  }




//  public void init() {
//    try {
//      // Lấy nội dung từ URL
//      RestTemplate restTemplate = new RestTemplate();
//      String base64Token = restTemplate.getForObject(tokenUrl, String.class);
//
//      // Kiểm tra nếu nội dung trả về không null
//      if (base64Token != null) {
//        base64Token = base64Token.trim(); // Loại bỏ khoảng trắng đầu/cuối
//        System.out.println("Base64 Token Retrieved: " + base64Token);
//
//        // Kiểm tra tính hợp lệ của Base64
//        if (isBase64(base64Token)) {
//          dropboxAccessToken = new String(Base64.getDecoder().decode(base64Token));
//          System.out.println("Dropbox Access Token: " + dropboxAccessToken);
//        } else {
//          System.err.println("Error: The retrieved token is not a valid Base64 string.");
//        }
//      } else {
//        System.err.println("Error: No token retrieved from URL.");
//      }
//    } catch (Exception e) {
//      System.err.println("Error during initialization: " + e.getMessage());
//    }
//  }

//  private boolean isBase64(String str) {
//    try {
//      // Kiểm tra khả năng giải mã Base64
//      Base64.getDecoder().decode(str);
//      return true;
//    } catch (IllegalArgumentException e) {
//      return false;
//    }
//  }


  private DbxClientV2 getClient() {
    DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/edutechit_api/files").build();
    return new DbxClientV2(config, dropboxAccessToken.trim());
  }

  public String uploadFile(InputStream in, String fileName) throws Exception {
    DbxClientV2 client = getClient();
    FileMetadata metadata = client.files().uploadBuilder("/" + fileName)
        .withMode(WriteMode.OVERWRITE)
        .uploadAndFinish(in);
    return metadata.getPathLower();
  }

  private String formatDropboxLink(String url, boolean isPdf) {
    if (isPdf) {
      return url.contains("?") ? url.replaceFirst("([?&])(dl=0|dl=1)", "$1raw=1") : url + "?raw=1";
    } else {
      return url.contains("?") ? url.replaceFirst("([?&])dl=0", "$1dl=1") : url + "?dl=1";
    }
  }

  public String getSharedLink(String filePath) throws Exception {
    DbxClientV2 client = getClient();
    boolean isPdf = filePath.endsWith(".pdf");

    try {
      SharedLinkMetadata sharedLinkMetadata = client.sharing()
          .createSharedLinkWithSettings(filePath);
      return formatDropboxLink(sharedLinkMetadata.getUrl(), isPdf);
    } catch (Exception e) {
      var links = client.sharing().listSharedLinksBuilder()
          .withPath(filePath)
          .withDirectOnly(true)
          .start()
          .getLinks();

      if (links.isEmpty()) {
        throw new Exception("No shared link found for the specified file path.");
      }
      return formatDropboxLink(links.get(0).getUrl(), isPdf);
    }
  }

  public String uploadPdfAndGetLink(InputStream in, String fileName) throws Exception {
    String filePath = uploadFile(in, fileName);
    return getSharedLink(filePath);
  }

  public void deleteFile(String filePath) throws Exception {
    DbxClientV2 client = getClient();
    try {
      // Ensure the file path is in the correct format for Dropbox API
      if (filePath.startsWith("https://www.dropbox.com")) {
        throw new IllegalArgumentException(
            "Invalid file path. Expected a Dropbox file path, not a shared link.");
      }
      // Delete file at the specified file path
      client.files().deleteV2(filePath);
      System.out.println("File deleted successfully: " + filePath);
    } catch (Exception e) {
      throw new Exception("Failed to delete file: " + filePath, e);
    }
  }

}