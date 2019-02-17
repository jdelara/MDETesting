package anatlyzer.testing.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public interface IModel {

	@NonNull
	Resource getResource();

	@Nullable
	public Metamodel getMetamodel();
	
	void save() throws IOException;
	
	@Nullable
	<T> T getAttribute(Class<T> key);
	
	public static abstract class AbstractModel implements IModel {
		private Map<Class<?>, Object> attributes = new HashMap<Class<?>, Object>();
		
		@NonNull
		public <T> T getAttribute(@NonNull Class<T> key) {
			Object value = attributes.get(key);
			if ( value == null )
				throw new IllegalArgumentException();
			return key.cast(value);
		}
		
		@Nullable
		public <T> T getAttributeOrNull(@NonNull Class<T> key) {
			Object value = attributes.get(key);
			if ( value == null )
				return null;
			
			return key.cast(value);
		}
		
		
		public void addAttribute(@NonNull Class<?> key, @NonNull Object value) {
			this.attributes.put(key, value);
		}
	}

}
