package modelo.factory;

import modelo.carta.*;

public class RegistroCartas {

    public static CartaFactory crearFactory() {

        CartaFactory factory =
                new CartaFactory();

        factory.registrar(
                "PotOfGreed",
                PotOfGreed.class);

        factory.registrar(
                "Raigeki",
                Raigeki.class);

        factory.registrar(
                "DarkHole",
                DarkHole.class);

        factory.registrar(
                "Hinotama",
                Hinotama.class);

        factory.registrar(
                "ChangeOfHeart",
                ChangeOfHeart.class);

        factory.registrar(
                "StandarOfCourage",
                StandarOfCourage.class);

        factory.registrar(
                "TyphoonOfMagicalSpace",
                TyphoonOfMagicalSpace.class);

        return factory;
    }
}
