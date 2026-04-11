package aptech.proj_NN_group2.model.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;

//define generic interface
//using template--> class mapping
public interface IMapper<T> {
	T RowMap(ResultSet rs) throws SQLException;
}