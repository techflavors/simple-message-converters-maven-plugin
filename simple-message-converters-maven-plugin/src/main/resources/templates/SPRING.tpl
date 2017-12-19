package $package;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import techflavors.tools.smc.converters.MessageConverter;
import techflavors.tools.smc.converters.MessageConverters;

@Configuration
@ComponentScan(basePackages = { "$package" })
public class $cname {

	@Bean(name = "$mcname")
	public MessageConverters converters() {
		return new MessageConverters("$mcname");
	}
	
	#foreach($converter in $converters) 
	
	@Bean(name = "${converter}")
	public MessageConverter<?,?> ${converter}() {
		return new ${converter}();
	}
	#end
	

}