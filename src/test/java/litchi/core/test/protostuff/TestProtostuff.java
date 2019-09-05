package litchi.core.test.protostuff;

import litchi.core.net.rpc.serializer.ProtoStuffSerializer;
import litchi.core.net.rpc.serializer.Serializer;

import java.io.IOException;

public class TestProtostuff {

	private static Serializer SERIALIZER = new ProtoStuffSerializer();

	public static void main(String[] args) throws IOException {
		BaseClazz baseClazz = new BaseClazz();
		AClazz a = new AClazz();
		a.aInt = 11;
		a.aString = "aString";

		baseClazz.id = 1000L;
		baseClazz.subClazz = a;

		byte[] byteAClazz = SERIALIZER.encode(baseClazz);

		BaseClazz bClazz = SERIALIZER.decode(byteAClazz, BaseClazz.class);
		if (bClazz.subClazz instanceof AClazz) {
			//System.out.println("ok");
		}
	}

}
