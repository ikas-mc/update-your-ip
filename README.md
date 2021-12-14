# update your ip

upload your ipv6 ip to github repo



# build 
1. change ipv6 prefix   
https://github.com/ikas-mc/update-your-ip/blob/5e47fba7f7606cc798ef2a3a301d8dabb03d8a7d/src/main/java/ikas/java/project/updateip/IpUtil.java#L18

2. mvn package


# use
create repo and upload deploy key

##  config
```
#repo path
path=
#repo url
url=
#ssh key
key=
#update period (MINUTES)
time=1
```

# update ip to host

save this code to UpdateIp.java

```java

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class UpdateIp{
    public static void main(String[] args) throws IOException {
        var host= Paths.get("C:\\Windows\\System32\\drivers\\etc\\hosts");
        var lines= Files.readAllLines(host,StandardCharsets.US_ASCII);

        var  index=-1;
        for (int i = 0; i < lines.size(); i++) {
            if(lines.get(i).endsWith("#yuki-mc-server")){
                index=i;
                break;
            }
        }

        var ip=Files.readString(Paths.get("your-ip\\ip"), StandardCharsets.UTF_8);
        var line= String.format("%s yuki-mc #yuki-mc-server",ip);

        if(index!=-1){
            lines.set(index,line);
        }else{
            lines.add(line);
        }

        Files.writeString(host,String.join("\r\n",lines),StandardCharsets.US_ASCII, StandardOpenOption.TRUNCATE_EXISTING);
    }
}


```

update.bat
```
cd your-ip-repo
git pull
cd..
java UpdateIp.java

```
