package $package;

import techflavors.tools.smc.converters.MessageConverter;

public class $class extends
		MessageConverter<$source, $target> {

	@Override
	protected $target toTargetType(
			$source data) {
		$target result = new $target();
		return result;
	}

	@Override
	protected $source toSourceType(
			$target data) {
		$source result = new $source();
		return result;
	}
}