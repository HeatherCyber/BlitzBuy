/**
 * Session management utility for handling user authentication state
 */

export class SessionManager {
  private static readonly USER_TICKET_KEY = 'userTicket';
  private static readonly USER_INFO_KEY = 'userInfo';

  /**
   * Store user ticket after successful login
   */
  public static setUserTicket(ticket: string): void {
    localStorage.setItem(this.USER_TICKET_KEY, ticket);
  }

  /**
   * Get stored user ticket
   */
  public static getUserTicket(): string | null {
    return localStorage.getItem(this.USER_TICKET_KEY);
  }

  /**
   * Store user information
   */
  public static setUserInfo(userInfo: any): void {
    localStorage.setItem(this.USER_INFO_KEY, JSON.stringify(userInfo));
  }

  /**
   * Get stored user information
   */
  public static getUserInfo(): any | null {
    const userInfo = localStorage.getItem(this.USER_INFO_KEY);
    return userInfo ? JSON.parse(userInfo) : null;
  }

  /**
   * Check if user is logged in
   */
  public static isLoggedIn(): boolean {
    return this.getUserTicket() !== null;
  }

  /**
   * Clear all session data (logout)
   */
  public static clearSession(): void {
    localStorage.removeItem(this.USER_TICKET_KEY);
    localStorage.removeItem(this.USER_INFO_KEY);
  }

  /**
   * Get authorization header for API requests
   */
  public static getAuthHeader(): { Authorization?: string } {
    const ticket = this.getUserTicket();
    return ticket ? { Authorization: `Bearer ${ticket}` } : {};
  }
}
