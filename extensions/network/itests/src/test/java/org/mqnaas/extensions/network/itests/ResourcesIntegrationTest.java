package org.mqnaas.extensions.network.itests;

import java.io.File;
import java.util.Arrays;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceManagement;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.network.api.topology.link.ILinkAdministration;
import org.mqnaas.network.api.topology.link.ILinkManagement;
import org.mqnaas.network.api.topology.port.IPortManagement;
import org.mqnaas.network.impl.topology.link.LinkResource;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ResourcesIntegrationTest {

	@Inject
	IRootResourceManagement	rootResourceMgmt;

	@Inject
	IServiceProvider		serviceProvider;

	@Configuration
	public Option[] config() {
		// FIXME Read mqnass features version from maven.
		// now mqnaas features version in this file must be changed manually in each release!
		return new Option[] {
				// distribution to test: Karaf 3.0.1
				KarafDistributionOption.karafDistributionConfiguration()
						.frameworkUrl(CoreOptions.maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("tar.gz").version("3.0.1"))
						.karafVersion("3.0.1").name("Apache Karaf").useDeployFolder(false)
						// keep deployed Karaf
						.unpackDirectory(new File("target/paxexam")),
				// no local and remote consoles
				KarafDistributionOption.configureConsole().ignoreLocalConsole(),
				KarafDistributionOption.configureConsole().ignoreRemoteShell(),
				// keep runtime folder allowing analysing results
				KarafDistributionOption.keepRuntimeFolder(),
				// use custom logging configuration file with a custom appender
				KarafDistributionOption.replaceConfigurationFile("etc/org.ops4j.pax.logging.cfg", new File(
						"src/test/resources/org.ops4j.pax.logging.cfg")),
				// maintain our log configuration
				KarafDistributionOption.doNotModifyLogConfiguration(),
				// add network feature
				KarafDistributionOption.features(CoreOptions.maven().groupId("org.mqnaas.extensions").artifactId("network").classifier("features")
						.type("xml").version("0.0.1-SNAPSHOT"), "network"),
		// debug option
		// KarafDistributionOption.debugConfiguration()
		};
	}

	@Test(expected = CapabilityNotFoundException.class)
	public void portManagementCoreBindingTest() throws CapabilityNotFoundException {

		IRootResource rootResource = createRootResource(Type.CORE);

		serviceProvider.getCapability(rootResource, IPortManagement.class);
	}

	@Test(expected = CapabilityNotFoundException.class)
	public void portManagementNetworkBindingTest() throws CapabilityNotFoundException {

		// network resource
		IRootResource rootResource = createRootResource(Type.NETWORK);
		serviceProvider.getCapability(rootResource, IPortManagement.class);
	}

	@Test
	public void portManagementRestOfResourcesBindingTest() throws CapabilityNotFoundException {

		// network resource
		IRootResource rootResource = createRootResource(Type.TSON);
		IPortManagement portManagementCapab = serviceProvider.getCapability(rootResource, IPortManagement.class);

		Assert.assertNotNull("All RootResources, except network and core, should contain a bound IPortManagement capability.",
				portManagementCapab);

		// remove created resources
		rootResourceMgmt.removeRootResource(rootResource);
	}

	@Test(expected = CapabilityNotFoundException.class)
	public void linkManagementCoreBindingTest() throws CapabilityNotFoundException {
		IRootResource rootResource = createRootResource(Type.CORE);

		serviceProvider.getCapability(rootResource, ILinkManagement.class);
	}

	@Test
	public void linkManagementNetworkBindingTest() throws CapabilityNotFoundException {

		// network resource
		IRootResource rootResource = createRootResource(Type.NETWORK);
		ILinkManagement linkManagementCapab = serviceProvider.getCapability(rootResource, ILinkManagement.class);
		Assert.assertNotNull("Network resource should contain a bound ILinkManagement capability.", linkManagementCapab);
		rootResourceMgmt.removeRootResource(rootResource);

		// other resource (for example, tson)
		rootResource = createRootResource(Type.TSON);
		linkManagementCapab = serviceProvider.getCapability(rootResource, ILinkManagement.class);
		Assert.assertNotNull("All RootResources should contain a bound ILinkManagement capability.",
				linkManagementCapab);

		// remove created resources
		rootResourceMgmt.removeRootResource(rootResource);

	}

	@Test
	public void linkAdministrationBindingTest() throws CapabilityNotFoundException {

		IRootResource rootResource = createRootResource(Type.TSON);

		ILinkManagement linkManagementCapab = serviceProvider.getCapability(rootResource, ILinkManagement.class);
		Assert.assertNotNull("TSON resource should contain a bound ILinkManagement capability.", linkManagementCapab);

		// link resource capabilities
		IResource link = linkManagementCapab.createLink();
		Assert.assertNotNull(link);
		Assert.assertTrue(link instanceof LinkResource);

		ILinkAdministration linkAdminCapab = serviceProvider.getCapability(link, ILinkAdministration.class);
		Assert.assertNotNull("Link resource should contain a bound ILinkAdministration capability", linkAdminCapab);

		// remove created resources
		linkManagementCapab.removeLink(link);
		rootResourceMgmt.removeRootResource(rootResource);

	}

	private IRootResource createRootResource(Specification.Type resourceType) {

		// core resource
		Specification spec = new Specification(resourceType);
		Endpoint endpoint = null;

		return rootResourceMgmt.createRootResource(spec, Arrays.asList(endpoint));

	}
}