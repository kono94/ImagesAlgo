package mvc;

@SuppressWarnings("serial")
public class Controller {
	private View view;
	private Model m_Model;
	private int m_SwitchingDelay;
	
	
	public Controller(){
		Model m_model = new Model();
		view = new View(m_model);
		new MyFileChooser(m_model, view);
		applyMenuListeners();
		
	}
	
	public void applyMenuListeners() {
		// load in a single image
		view.getMyMenuBar().getMIopen().addActionListener(e -> {
			
		});
		
		// load all images from working directory
		view.getMyMenuBar().getMILoadAllImagesItem().addActionListener(e -> {
			
		});
		
		// switch to slow transition speed
		view.getMyMenuBar().getMIslow().addActionListener(e ->{
			m_SwitchingDelay = 1000;
		});
		
		// switch to medium transition speed
		view.getMyMenuBar().getMImedium().addActionListener(e->{
			m_SwitchingDelay = 500;
		});
		
		// switch to ultra transition speed
		view.getMyMenuBar().getMIveryFast().addActionListener(e -> {
			m_SwitchingDelay = 20;
		});
	}
}
