export class UserNameUtil {
  public static getDisplayName(user:any): string {
    if(user.lastName && user.firstName){
      return `${user.lastName.substring(0, 1)}.${user.firstName}`;
    }
    if(user.firstName){
      return user.firstName;
    }
    else {
      return undefined;
    }
  }

  public static getCombinedName(displayName: string, name: string) {
    if(displayName ==  name || !displayName){
      return name;
    }
    if(displayName && !name){
      return displayName;
    }
    else {
      return `${displayName} ${this.getNameInBrackets(name)}`;
    }
  }

  public static getNameInBrackets(username: string): string {
    return  `/${username}/`;
  }
}
