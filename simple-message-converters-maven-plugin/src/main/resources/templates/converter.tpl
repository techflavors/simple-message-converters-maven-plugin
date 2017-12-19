package $package;

import techflavors.tools.smc.converters.MessageConverter;

public class $class extends
		MessageConverter<$source, $target> {

	@SuppressWarnings({ "unchecked"})
	@Override
	protected $target toTargetType(
			$source data) {
		$target result = new $target();
		
		#setters($tmethods,"Target")
   		 
		return result;
	}

	@SuppressWarnings({ "unchecked"})
	@Override
	protected $source toSourceType(
			$target data) {
		 $source result = new $source();
		
		 #setters($smethods,"Source")
   		 
  		return result;
	}
	
	#macro(setters $methods, $dir)
		#foreach($method in $methods)
			#if($method.paramType == "P")
    	result.set${method.name}(data.get${method.name}());
			#elseif($method.paramType == "O")
    result.set${method.name}(($method.type)messageConverters.to${dir}Type(data.get${method.name}()));
			#elseif($method.paramType == "C")
    result.set${method.name}((${method.gtype}<$method.type>) (${method.gtype}<?>) messageConverters.to${dir}TypeAsCollection(data.get${method.name}()));
    			#elseif($method.paramType == "A")
    result.set${method.name}((${method.type}[]) messageConverters.to${dir}TypeAsArray(data.get${method.name}()));
			#end
		#end
   #end
	
}