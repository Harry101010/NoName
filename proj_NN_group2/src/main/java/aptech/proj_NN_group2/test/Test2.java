package aptech.proj_NN_group2.test;

import aptech.proj_NN_group2.model.business.repository.RoleRepository;
import aptech.proj_NN_group2.model.entity.Role;

public class Test2 {
	public static void main(String[] args) {
		RoleRepository repo = new RoleRepository();
		System.out.println(repo.findById(10));
		System.out.println("___________________________");
		for (Role r : repo.findAll()) {
			System.out.println(r);
		}
	}
}