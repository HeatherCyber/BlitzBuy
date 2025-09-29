import CryptoJS from 'crypto-js';

/**
 * MD5 utility for client-side password hashing
 * Matches the backend MD5Util.java implementation
 */
export class MD5Util {
  // Static salt string from backend (matches MD5Util.java)
  private static readonly SALT = "4tIY5VcX";

  /**
   * Generate MD5 hash of input string
   */
  public static md5(src: string): string {
    return CryptoJS.MD5(src).toString();
  }

  /**
   * Transfer original password to medium password (client-side hashing)
   * This matches the backend inputPassToMidPass method
   */
  public static inputPassToMidPass(inputPass: string): string {
    const str = this.SALT.charAt(0) + inputPass + this.SALT.charAt(6);
    return this.md5(str);
  }

  /**
   * Transfer medium password to database password (server-side hashing)
   * This matches the backend midPassToDBPass method
   * Note: This is typically done on the server, but included for completeness
   */
  public static midPassToDBPass(midPass: string, salt: string): string {
    const str = salt.charAt(2) + midPass + salt.charAt(4);
    return this.md5(str);
  }

  /**
   * Transfer original password directly to database password
   * This matches the backend inputPassToDBPass method
   * Note: This is typically done on the server, but included for completeness
   */
  public static inputPassToDBPass(inputPass: string, salt: string): string {
    const midPass = this.inputPassToMidPass(inputPass);
    return this.midPassToDBPass(midPass, salt);
  }
}
