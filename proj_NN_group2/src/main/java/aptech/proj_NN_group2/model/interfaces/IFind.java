package aptech.proj_NN_group2.model.interfaces;

import java.util.List;

public interface IFind<T> {
	T findById(int id);
	List<T> findAll();
}