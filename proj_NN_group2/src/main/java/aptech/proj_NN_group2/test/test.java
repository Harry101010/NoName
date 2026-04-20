package aptech.proj_NN_group2.test;
import org.mindrot.jbcrypt.BCrypt;



public class test{
    public static void main(String[] args) {
        String hash = BCrypt.hashpw("123456", BCrypt.gensalt());
        System.out.println(hash);
    }
}