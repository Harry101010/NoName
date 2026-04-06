package aptech.proj_NN_group2.test;

import aptech.proj_NN_group2.model.business.repository.UserRepository;
import aptech.proj_NN_group2.model.entity.User;

public class Test1 {
	public static void main(String[] args) {
		UserRepository repo = new UserRepository();
		System.out.println(repo.findById(10));
		System.out.println("___________________________");
		for (User u : repo.findAll()) {
			System.out.println(u);
		}
	}
}