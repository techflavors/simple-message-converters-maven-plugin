package $package;

import techflavors.tools.smc.converters.MessageConverter;
import techflavors.tools.smc.converters.MessageConverters;

import java.util.List;
import java.util.ArrayList;

public final class $cname {
	
	private static final MessageConverters messageConverters = new MessageConverters("$mcname");
	
	static {
		List<MessageConverter<?, ?>> messageConverterList = new ArrayList<>();
		#foreach($converter in $converters) 
		messageConverterList.add(new ${converter}());
		#end

		messageConverters.setMessageConverters(messageConverterList);
	}

	public static final MessageConverters messageConvertors(){
		return messageConverters;
	}
	
	private ${cname}(){}

}