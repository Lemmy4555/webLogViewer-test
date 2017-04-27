export class CacheHelper {
    private static readonly keys = {
      lastOpenedFile: "last-opened-file"
    }

    public static getLastOpenedFile() {
      return localStorage[CacheHelper.keys.lastOpenedFile];
    }
    
    public static setLastOpenedFile(lastOpenedFile: string) {
      localStorage[CacheHelper.keys.lastOpenedFile] = lastOpenedFile;
    }
}