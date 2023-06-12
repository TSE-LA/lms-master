package mn.erin.lms.base.analytics.repository.mongo.constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import mn.erin.lms.base.analytics.repository.mongo.utils.ArrayStringConverter;

/**
 * @author Munkh
 */
public class ArrayStringConverterTest
{
  @Test
  public void converts_set_to_string()
  {
    Set<String> set = new HashSet<>();
    set.add("string1");
    set.add("string2");
    Assert.assertEquals("[\"string1\", \"string2\"]", ArrayStringConverter.toArrayString(set));
  }

  @Test
  public void converts_arrayList_to_string()
  {
    List<String> arrayList = new ArrayList<>();
    arrayList.add("string1");
    arrayList.add("string2");
    Assert.assertEquals("[\"string1\", \"string2\"]", ArrayStringConverter.toArrayString(arrayList));
  }

  @Test(expected = NullPointerException.class)
  public void convert_null_collection_throws_nullPointer()
  {
    ArrayStringConverter.toArrayString(null);
  }

  @Test
  public void convert_empty_list_test()
  {
    List<String> arrayList = new ArrayList<>();
    Assert.assertEquals("[]", ArrayStringConverter.toArrayString(arrayList));
  }

  @Test
  public void convert_single_item_list_test()
  {
    Set<String> set = new HashSet<>();
    set.add("string");
    Assert.assertEquals("[\"string\"]", ArrayStringConverter.toArrayString(set));
  }

  @Test
  public void convert_null_item_list_test()
  {
    Set<String> set = new HashSet<>();
    set.add(null);
    Assert.assertEquals("[]", ArrayStringConverter.toArrayString(set));
  }
}
