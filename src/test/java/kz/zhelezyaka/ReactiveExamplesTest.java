package kz.zhelezyaka;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ReactiveExamplesTest {

    Person robert = new Person("Robert", "Baratheon");
    Person jaime = new Person("Jaime", "Lannister");
    Person catelyn = new Person("Catelyn", "Stark");
    Person jon = new Person("Jon", "Snow");
    Person ramsay = new Person("Ramsay", "Bolton");

    @Test
    public void monoTests() {
        Mono<Person> personMono = Mono.just(robert);
        Person person = personMono.block();
        log.info(person.sayMyName());
    }

    @Test
    public void monoTransform() {
        Mono<Person> personMono = Mono.just(jaime);
        PersonCommand command = personMono
                .map(person -> new PersonCommand(person)).block();
        log.info(command.sayMyName());
    }

    @Test(expected = NullPointerException.class)
    public void monoFilter() {
        Mono<Person> personMono = Mono.just(catelyn);
        Person catelynStark = personMono
                .filter(person -> person.getFirstName().equalsIgnoreCase("foo"))
                .block();
        log.info(catelynStark.sayMyName());
    }

    @Test
    public void fluxTest() {
        Flux<Person> people = Flux.just(robert, jaime, catelyn, jon, ramsay);
        people.subscribe(person -> log.info(person.sayMyName()));
    }

    @Test
    public void fluxTestFilter() {
        Flux<Person> people = Flux.just(robert, jaime, catelyn, jon, ramsay);
        people.filter(person -> person.getFirstName().equals(robert.getFirstName()))
                .subscribe(person -> log.info(person.sayMyName()));
    }

    @Test
    public void fluxTestDelayNoOutput() {
        Flux<Person> people = Flux.just(robert, jaime, catelyn, jon, ramsay);
        people.delayElements(Duration.ofSeconds(1))
                .subscribe(person -> log.info(person.sayMyName()));
    }

    @Test
    public void fluxTestDelay() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux<Person> people = Flux.just(robert, jaime, catelyn, jon, ramsay);
        people.delayElements(Duration.ofSeconds(1))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));
        countDownLatch.await();
    }

    @Test
    public void fluxTestFilterDelay() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux<Person> people = Flux.just(robert, jaime, catelyn, jon, ramsay);
        people.delayElements(Duration.ofSeconds(1))
                .filter(person -> person.getFirstName().contains("y"))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));
        countDownLatch.await();
    }
}
