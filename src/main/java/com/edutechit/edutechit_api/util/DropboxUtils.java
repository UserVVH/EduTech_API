package com.edutechit.edutechit_api.util;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DropboxUtils {


  @Value("${dropbox.access.token}")
  private String accessToken;


  private DbxClientV2 getClient() {
    DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/eduvnuahtech_api/files").build();
    return new DbxClientV2(config, accessToken.trim());
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