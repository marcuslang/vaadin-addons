package org.rapidpm.vaadin.addons.testbench;

import com.google.gson.stream.JsonReader;
import com.vaadin.testbench.TestBench;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.dependencies.core.logger.Logger;
import org.rapidpm.frp.Transformations;
import org.rapidpm.frp.functions.CheckedExecutor;
import org.rapidpm.frp.functions.CheckedFunction;
import org.rapidpm.frp.functions.CheckedPredicate;
import org.rapidpm.frp.functions.CheckedSupplier;
import org.rapidpm.frp.model.Result;
import org.rapidpm.frp.model.serial.Pair;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.System.setProperty;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.rapidpm.frp.StringFunctions.notEmpty;
import static org.rapidpm.frp.StringFunctions.notStartsWith;
import static org.rapidpm.frp.Transformations.not;
import static org.rapidpm.frp.matcher.Case.match;
import static org.rapidpm.frp.matcher.Case.matchCase;
import static org.rapidpm.frp.memoizer.Memoizer.memoize;
import static org.rapidpm.frp.model.Result.*;

/**
 *
 *
 */
public interface BrowserDriverFunctions extends HasLogger {

  String BROWSER_NAME = "browserName";
  String PLATFORM     = "platform";
  String UNITTESTING  = "unittesting";
  String ENABLE_VNC   = "enableVNC";
  String VERSION      = "version";
  String ENABLE_VIDEO = "enableVideo";


  String SELENIUM_GRID_PROPERTIES_LOCALE_IP      = "locale-ip";
  String SELENIUM_GRID_PROPERTIES_LOCALE_BROWSER = "locale";
  String SELENIUM_GRID_PROPERTIES_NO_GRID        = "nogrid";

  static Supplier<String> localeIP() {
    return () -> {
      final CheckedSupplier<Enumeration<NetworkInterface>> checkedSupplier = NetworkInterface::getNetworkInterfaces;

      return Transformations.<NetworkInterface>enumToStream()
          .apply(checkedSupplier.getOrElse(Collections::emptyEnumeration))
          .filter((CheckedPredicate<NetworkInterface>) NetworkInterface::isUp)
          .map(NetworkInterface::getInetAddresses)
          .flatMap(iaEnum -> Transformations.<InetAddress>enumToStream().apply(iaEnum))
          .filter(inetAddress -> inetAddress instanceof Inet4Address)
          .filter(not(InetAddress::isMulticastAddress))
          .filter(not(InetAddress::isLoopbackAddress))
          .map(InetAddress::getHostAddress)
          .filter(notEmpty())
          .filter(adr -> notStartsWith().apply(adr, "127"))
          .filter(adr -> notStartsWith().apply(adr, "169.254"))
          .filter(adr -> notStartsWith().apply(adr, "255.255.255.255"))
          .filter(adr -> notStartsWith().apply(adr, "255.255.255.255"))
          .filter(adr -> notStartsWith().apply(adr, "0.0.0.0"))
          //            .filter(adr -> range(224, 240).noneMatch(nr -> adr.startsWith(valueOf(nr))))
          .findFirst().orElse("localhost");
    };
  }

  static CheckedFunction<String, Properties> propertyReaderMemoized() {
    return (CheckedFunction<String, Properties>) memoize(propertyReader());
  }

  static CheckedFunction<String, Properties> propertyReader() {
    return (filename) -> {
      try (
          final FileInputStream fis = new FileInputStream(new File(filename));
          final BufferedInputStream bis = new BufferedInputStream(fis)) {
        final Properties properties = new Properties();
        properties.load(bis);

        return properties;
      } catch (IOException e) {
        e.printStackTrace();
        throw e;
      }
    };
  }

  static CheckedExecutor readTestbenchProperties() {
    return () -> propertyReader()
        .apply("config/testbench.properties")
        .ifPresent(p -> p.forEach((key, value) -> setProperty((String) key, (String) value))
        );
  }

  static Supplier<Properties> readSeleniumGridProperties() {
    return () -> propertyReader()
        .apply("config/selenium-grids.properties")
        .getOrElse(Properties::new);
  }


  static Function<String, Result<WebDriver>> localWebDriverInstance() {
    return browserType -> {
      readTestbenchProperties().execute();
      return match(
          matchCase(() -> success(new PhantomJSDriver())),
          matchCase(browserType::isEmpty, () -> Result.failure("browserTape should not be empty")),
          matchCase(() -> browserType.equals(BrowserType.PHANTOMJS), () -> success(new PhantomJSDriver())),
          matchCase(() -> browserType.equals(BrowserType.FIREFOX), () -> success(new FirefoxDriver())),
          matchCase(() -> browserType.equals(BrowserType.CHROME), () -> success(new ChromeDriver())),
          matchCase(() -> browserType.equals(BrowserType.SAFARI), () -> success(new SafariDriver())),
          matchCase(() -> browserType.equals(BrowserType.OPERA), () -> success(new OperaDriver())),
          matchCase(() -> browserType.equals(BrowserType.OPERA_BLINK), () -> success(new OperaDriver())),
          matchCase(() -> browserType.equals(BrowserType.IE), () -> success(new InternetExplorerDriver()))
      );
    };
  }

  static Function<String, Result<DesiredCapabilities>> type2Capabilities() {
    return (browsertype) ->
        match(
            matchCase(() -> failure("browsertype unknown : " + browsertype)),
            matchCase(browsertype::isEmpty, () -> Result.failure("browsertype should not be empty")),
            matchCase(() -> browsertype.equals(BrowserType.PHANTOMJS), () -> success(DesiredCapabilities.phantomjs())),
            matchCase(() -> browsertype.equals(BrowserType.FIREFOX), () -> success(DesiredCapabilities.firefox())),
            matchCase(() -> browsertype.equals(BrowserType.CHROME), () -> success(DesiredCapabilities.chrome())),
            matchCase(() -> browsertype.equals(BrowserType.EDGE), () -> success(DesiredCapabilities.edge())),
            matchCase(() -> browsertype.equals(BrowserType.SAFARI), () -> success(DesiredCapabilities.safari())),
            matchCase(() -> browsertype.equals(BrowserType.OPERA_BLINK), () -> success(DesiredCapabilities.operaBlink())),
            matchCase(() -> browsertype.equals(BrowserType.OPERA), () -> success(DesiredCapabilities.opera())),
            matchCase(() -> browsertype.equals(BrowserType.IE), () -> success(DesiredCapabilities.internetExplorer()))
        );
  }

  static Supplier<Result<DesiredCapabilities>> readDefaultDesiredCapability() {
    return () -> ofNullable(readDesiredCapabilities()
                                .get()
                                .stream()
                                .map(desiredCapabilitiesList -> desiredCapabilitiesList
                                    .stream()
                                    .filter(dc -> (dc.getCapability(UNITTESTING) != null)
                                                  ? Boolean.valueOf(dc.getCapability(UNITTESTING).toString())
                                                  : Boolean.FALSE)
                                    .collect(toList()))
                                .filter(l -> l.size() == 1)
                                .findFirst()
                                .orElse(emptyList())
                                .get(0), "too many or no default Browser specified..");
  }

  static Supplier<Result<List<DesiredCapabilities>>> readDesiredCapabilities() {
    return () -> {
      final List<DesiredCapabilities> result = new ArrayList<>();
      final File                      file   = new File("config/browser_combinations.json");
      try (
          final FileReader fr = new FileReader(file);
          final JsonReader reader = new JsonReader(fr)) {

        reader.beginObject();
        while (reader.hasNext()) {
          String name = reader.nextName();
          if (name.equals("browsers")) {
            reader.beginArray();
            while (reader.hasNext()) {
              reader.beginObject();
              String                    browser     = "";
              String                    version     = "";
              Platform                  platform    = Platform.ANY;
              final Map<String, Object> noNameProps = new HashMap<>();
              while (reader.hasNext()) {
                String property = reader.nextName();
                switch (property) {
                  case BROWSER_NAME:
                    browser = reader.nextString();
                    break;
                  case PLATFORM:
                    platform = Platform.fromString(reader.nextString());
                    break;
                  case VERSION:
                    version = reader.nextString();
                    break;
                  case ENABLE_VIDEO:
                    noNameProps.put(property, reader.nextBoolean());
                    break;
                  case UNITTESTING:
                    noNameProps.put(property, reader.nextBoolean());
                    break;
                  case ENABLE_VNC:
                    noNameProps.put(property, reader.nextBoolean());
                    break;
                  default:
                    noNameProps.put(property, reader.nextString());
                    break;
                }
              }

              final Platform platformFinal = platform;
              final String   versionFinal  = version;

              type2Capabilities()
                  .apply(browser)
                  .ifPresentOrElse(
                      success -> {
                        success.setPlatform(platformFinal);
                        success.setVersion(versionFinal);
                        noNameProps.forEach(success::setCapability);
                        result.add(success);
                      },
                      failed -> {
                      }
                  );
              ((CheckedExecutor) reader::endObject).execute();
            }
            reader.endArray();
          }
        }
        reader.endObject();
        reader.close();

      } catch (IOException e) {
        e.printStackTrace();
        return Result.failure(e.getMessage());
      }

      return Result.success(result);
    };
  }


  static Result<WebDriver> unittestingWebDriverInstance() {
    final String unittesting = readSeleniumGridProperties().get().getProperty(UNITTESTING);
    return (unittesting != null)
           ? match(
        matchCase(BrowserDriverFunctions::defaultRemoteWebDriverInstance),
        matchCase(unittesting::isEmpty,
                  () -> Result.failure(UNITTESTING + " should not be empty")
        ),
        matchCase(() -> unittesting.equals(SELENIUM_GRID_PROPERTIES_LOCALE_BROWSER),
                  BrowserDriverFunctions::defaultLocaleWebDriverInstance
        )
    )
           : Result.failure("no target for " + UNITTESTING + " could be found.");

  }

  static Result<WebDriver> defaultLocaleWebDriverInstance() {
    final Result<DesiredCapabilities> dcResult = readDefaultDesiredCapability().get();
    return (dcResult.isPresent())
           ? localWebDriverInstance().apply(dcResult.get().getBrowserName())
           : dcResult.asFailure();

  }

  static Result<WebDriver> defaultRemoteWebDriverInstance() {
    final Result<DesiredCapabilities> dcResult    = readDefaultDesiredCapability().get();
    final String                      unittesting = readSeleniumGridProperties().get().getProperty(UNITTESTING);

    return (dcResult.isPresent())
           ? (unittesting != null)
             ? webDriverInstance().apply(dcResult.get(), Pair.next(UNITTESTING, unittesting))
             : Result.failure(UNITTESTING + " should not be empty")
           : dcResult.asFailure();
  }


  static BiFunction<DesiredCapabilities, Pair<String, String>, Result<WebDriver>> webDriverInstance() {
    return (desiredCapability, pair) -> {
      final String key           = pair.getT1();
      final String targetAddress = pair.getT2();

      final String ip = (targetAddress.endsWith(SELENIUM_GRID_PROPERTIES_LOCALE_IP))
                        ? localeIP().get()
                        : targetAddress;
      return
          match(
              matchCase(() -> ((CheckedSupplier<WebDriver>) () -> {
                          final URL             url             = new URL("http://" + ip + ":4444/wd/hub");
                          final RemoteWebDriver remoteWebDriver = new RemoteWebDriver(url, desiredCapability);
                          return TestBench.createDriver(remoteWebDriver);
                        }).get()
              ),
              matchCase(() -> key.equals(SELENIUM_GRID_PROPERTIES_NO_GRID),
                        () -> localWebDriverInstance()
                            .apply(desiredCapability.getBrowserName())
              )
          );
    };
  }


  static Supplier<List<WebDriver>> webDriverInstances() {
    return () -> {

      final Properties properties = readSeleniumGridProperties().get();

      return readDesiredCapabilities()
          .get()
          .getOrElse(Collections::emptyList) //TODO check if needed
          .stream()
          .map((desiredCapability) -> {

            //for all selenium ips
            return properties
                .entrySet()
                .stream()
                .map(e -> {
                  final String key           = (String) e.getKey();
                  final String targetAddress = (String) e.getValue();
                  return webDriverInstance().apply(desiredCapability, Pair.next(key, targetAddress));
                })
                .peek(r -> r.ifPresentOrElse(
                    success -> { },
                    failed -> {
                      Logger.getLogger(WebDriverFunctions.class).warning("failed = " + failed);
                      Logger.getLogger(WebDriverFunctions.class).warning("desiredCapability = " + desiredCapability);
                    }
                ))
                .filter(Result::isPresent)
                .collect(toList());
          })
          .flatMap(Collection::stream)
          .map(Result::get)
          .collect(toList());
    };
  }
}
