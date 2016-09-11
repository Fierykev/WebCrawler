package assignment;

import java.io.Serializable;

public class IndexTable implements Serializable, Comparable {
    private static final long serialVersionUID = 1L;
    
    public Integer position;
    
    public IndexTable(int position)
    {
        this.position = position;
    }

    @Override
    public int compareTo(Object o) {
        
        return position.compareTo((Integer) o);
    }
    
    
}