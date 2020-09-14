package litchi.core.dbqueue;

public class DbQueueModel {

	private ModelType modelType;
	
	private Object[] args;

	public DbQueueModel(ModelType modelType, Object... args) {
		super();
		this.modelType = modelType;
		this.args = args;
	}
	
	public ModelType getModelType() {
		return modelType;
	}
	
	public Object[] getArgs() {
		return args;
	}
}
