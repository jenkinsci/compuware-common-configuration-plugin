/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 - 2019 Compuware Corporation
 * (c) Copyright 2015 - 2019, 2021 BMC Software, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions: The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.compuware.jenkins.common.configuration;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCertificateCredentials;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.matchers.IdMatcher;
import com.compuware.jenkins.common.utils.ArgumentUtils;
import com.compuware.jenkins.common.utils.CLIVersionUtils;
import com.compuware.jenkins.common.utils.CommonConstants;
import com.compuware.jenkins.common.utils.NumericStringComparator;

import hudson.AbortException;
import hudson.CopyOnWrite;
import hudson.Extension;
import hudson.Launcher;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.model.Item;
import hudson.security.ACL;
import hudson.util.ArgumentListBuilder;
import hudson.util.ListBoxModel;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Class to handle Compuware global configuration settings.
 */
@Extension
public class CpwrGlobalConfiguration extends GlobalConfiguration
{
	// Constants
	private static final Logger LOGGER = Logger.getLogger("hudson.CpwrGlobalConfiguration"); //$NON-NLS-1$

	private static final String CODE_PAGE_MAPPINGS = "com.compuware.jenkins.common.configuration.codePageMappings"; //$NON-NLS-1$
	private static final String PROTOCOL_MAPPINGS = "com.compuware.jenkins.common.configuration.protocolMappings"; //$NON-NLS-1$
	/** Host connection instance ID defined in config.jelly */
	private static final String HOST_CONN_INSTANCE_ID = "hostConn"; //$NON-NLS-1$
	private static final String DESCRIPTION_ID = "description"; //$NON-NLS-1$
	private static final String HOST_PORT_ID = "hostPort"; //$NON-NLS-1$
	private static final String CODE_PAGE_ID = "codePage"; //$NON-NLS-1$
	private static final String PROTOCOL = "protocol"; //$NON-NLS-1$
	private static final String TIMEOUT_ID = "timeout"; //$NON-NLS-1$
	private static final String CONNECTION_ID = "connectionId"; //$NON-NLS-1$
	private static final String CES_URL_ID = "cesUrl"; //$NON-NLS-1$
	private static final String TOPAZ_CLI_LOCATION_WINDOWS_ID = "topazCLILocationWindows"; //$NON-NLS-1$
	private static final String TOPAZ_CLI_LOCATION_LINUX_ID = "topazCLILocationLinux"; //$NON-NLS-1$
	private static final String DEFAULT_TOPAZ_CLI_LOCATION_WINDOWS = "C:\\Program Files\\Compuware\\Topaz Workbench CLI"; //$NON-NLS-1$
	private static final String DEFAULT_TOPAZ_CLI_LOCATION_LINUX = "/opt/Compuware/TopazCLI"; //$NON-NLS-1$
	private static final String PROTOCOL_NONE = "None"; //$NON-NLS-1$
	
	// Member Variables
	@CopyOnWrite
	private volatile HostConnection[] m_hostConnections = new HostConnection[0];

	private String m_topazCLILocationWindows = DEFAULT_TOPAZ_CLI_LOCATION_WINDOWS;
	private String m_topazCLILocationLinux = DEFAULT_TOPAZ_CLI_LOCATION_LINUX;

	// Used to indicate if the configuration needs saving; used only in the context of migration.
	protected transient boolean m_needsSaving = false;

    /**
	 * Returns the singleton instance.
	 * 
	 * @return the Jenkins managed singleton for the configuration object
	 */ 
    public static CpwrGlobalConfiguration get() 
    { 
        return GlobalConfiguration.all().get(CpwrGlobalConfiguration.class); 
    }

	/**
	 * Constructor.
	 * <p>
	 * Clients should not call this - use {@link #get()} instead.
	 */
	public CpwrGlobalConfiguration()
	{
		load();
	}

	/**
	 * Return TRUE if the configuration needs saving.
	 * 
	 * @return TRUE if the configuration needs saving.
	 */
	public boolean needsSaving()
	{
		return m_needsSaving;
	}

	/**
	 * Perform initialization after all jobs have been loaded.
	 */
	@Initializer(after = InitMilestone.JOB_LOADED)
	public static void jobLoaded()
	{
		CpwrGlobalConfiguration globalConfig = CpwrGlobalConfiguration.get();
		if (globalConfig.needsSaving())
		{
			globalConfig.save();

			LOGGER.info("Compuware global configuration has been saved."); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the list of host connections.
	 * 
	 * @return list of host connections
	 */
	public HostConnection[] getHostConnections()
	{
		HostConnection[] hostConnections = new HostConnection[m_hostConnections.length];

		for (int i = 0; i < m_hostConnections.length; i++)
		{
			hostConnections[i] = new HostConnection(
				m_hostConnections[i].getDescription(),
				m_hostConnections[i].getHostPort(),
				m_hostConnections[i].getProtocol(),
				m_hostConnections[i].getCodePage(),
				m_hostConnections[i].getTimeout(),
				m_hostConnections[i].getConnectionId(),
				m_hostConnections[i].getCesUrl());
		}

		return hostConnections;
	}

	/**
	 * Returns a host connection for the given connection identifier.
	 * 
	 * @param connectionId
	 *            a unique connection identifier
	 * 
	 * @return a <code>HostConnection</code>; can be null
	 */
	public HostConnection getHostConnection(String connectionId)
	{
		HostConnection hostConnection = null;

		if (connectionId != null)
		{
			for (HostConnection connection : m_hostConnections)
			{
				if (connectionId.equalsIgnoreCase(connection.getConnectionId()))
				{
					hostConnection = connection;
					break;
				}
			}
		}

		return hostConnection;
	}

	/**
	 * Sets the list of host connections.
	 * 
	 * @param connections
	 *            one or more host connections
	 */
	public void setHostConnections(HostConnection... connections)
	{
		m_hostConnections = connections;
	}

	/**
	 * Returns a host connection for a given host:port and code page, if one exists.
	 * 
	 * @param hostPort
	 *            the host and port in the form of 'host:port'
	 * @param codePage
	 *            the code page
	 * 
	 * @return a host connection; can be null
	 */
	public HostConnection getHostConnection(String hostPort, String codePage)
	{
		HostConnection connection = null;

		String host = StringUtils.substringBefore(hostPort, CommonConstants.COLON);
		String port = StringUtils.substringAfter(hostPort, CommonConstants.COLON);
		for (HostConnection conn : m_hostConnections)
		{
			if (conn.getHost().equalsIgnoreCase(host) && conn.getPort().equalsIgnoreCase(port)
					&& conn.getCodePage().equalsIgnoreCase(codePage))
			{
				connection = conn;
				break;
			}
		}

		return connection;
	}

	/**
	 * Adds the given host connection to the list of host connections.
	 * 
	 * @param connection
	 *            connection to add
	 */
	public void addHostConnection(HostConnection connection)
	{
		List<HostConnection> newConnectionsList = new ArrayList<>(Arrays.asList(m_hostConnections));
		newConnectionsList.add(connection);
		setHostConnections(newConnectionsList.toArray(new HostConnection[newConnectionsList.size()]));
		m_needsSaving = true;
	}

	/* 
	 * (non-Javadoc)
	 * @see hudson.model.Descriptor#configure(org.kohsuke.stapler.StaplerRequest, net.sf.json.JSONObject)
	 */
	@Override
	public boolean configure(StaplerRequest req, JSONObject json)
	{
		HostConnection[] hostConnectionArray;
		Object jsonValue = json.get(HOST_CONN_INSTANCE_ID);

		if (req != null)
		{
			List<HostConnection> hostConnectionList = req.bindJSONToList(HostConnection.class, jsonValue);
			hostConnectionArray = new HostConnection[hostConnectionList.size()];
			hostConnectionArray = hostConnectionList.toArray(hostConnectionArray);
		}
		else
		{
			JSONArray jsonHostConnections = JSONArray.fromObject(jsonValue);
			hostConnectionArray = new HostConnection[jsonHostConnections.size()];
			
			for (int i = 0; i < jsonHostConnections.size(); i++)
			{
				JSONObject jsonHostConnection = jsonHostConnections.getJSONObject(i);
				hostConnectionArray[i] = new HostConnection(
					jsonHostConnection.getString(DESCRIPTION_ID),
					jsonHostConnection.getString(HOST_PORT_ID),
					jsonHostConnection.getString(PROTOCOL),
					jsonHostConnection.getString(CODE_PAGE_ID),
					jsonHostConnection.getString(TIMEOUT_ID),
					jsonHostConnection.getString(CONNECTION_ID),
					jsonHostConnection.getString(CES_URL_ID));
			}
		}

		setHostConnections(hostConnectionArray);

		m_topazCLILocationWindows = json.getString(TOPAZ_CLI_LOCATION_WINDOWS_ID);
		m_topazCLILocationLinux = json.getString(TOPAZ_CLI_LOCATION_LINUX_ID);

		save();

		return true;
	}

	/**
	 * Fills in the encryption protocol selection box with encryption protocols.
	 *
	 * @return encryption protocol selections
	 */
	public ListBoxModel doFillProtocolItems()
	{
		ListBoxModel protocolModel = new ListBoxModel();

		ResourceBundle cpBundle = ResourceBundle.getBundle(PROTOCOL_MAPPINGS);
		Set<String> protocolSet = cpBundle.keySet();

		// sort the encryption protocol values (for display purposes)
		List<String> protocolSetList = new ArrayList<>(protocolSet);
		Collections.sort(protocolSetList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if (PROTOCOL_NONE.equals(o1) || PROTOCOL_NONE.equals(o2)) {
					return -1;
				}
				return o1.compareTo(o2);
			}
		});

		Iterator<String> iterator = protocolSetList.iterator();
		while (iterator.hasNext())
		{
			String protocolId = iterator.next();
			String protocolDescription = cpBundle.getString(protocolId);

			protocolModel.add(protocolDescription, protocolId);
		}

		return protocolModel;
	}

	/**
	 * Fills in the Code page selection box with code pages.
	 *
	 * @return code page selections
	 */
	public ListBoxModel doFillCodePageItems()
	{
		ListBoxModel codePageModel = new ListBoxModel();

		ResourceBundle cpBundle = ResourceBundle.getBundle(CODE_PAGE_MAPPINGS);
		Set<String> cpNumberSet = cpBundle.keySet();

		// sort the code page values (for display purposes)
		List<String> cpNumberList = new ArrayList<>(cpNumberSet);
		Collections.sort(cpNumberList, new NumericStringComparator());

		Iterator<String> iterator = cpNumberList.iterator();
		while (iterator.hasNext())
		{
			String cpNumber = iterator.next();
			String cpDescription = cpBundle.getString(cpNumber);

			codePageModel.add(cpDescription, cpNumber);
		}

		return codePageModel;
	}

	/**
	 * Returns the Topaz Workbench CLI location based on node.
	 * 
	 * @param launcher
	 *            launcher for starting a process
	 * 
	 * @return the CLI location
	 */
	public String getTopazCLILocation(Launcher launcher)
	{
		if (launcher.isUnix())
		{
			return m_topazCLILocationLinux;
		}
		else
		{
			return m_topazCLILocationWindows;
		}
	}

	/**
	 * Returns the value of the topazCLILocationLinux. Used for databinding.
	 * 
	 * @return CLI location - Windows
	 */
	public String getTopazCLILocationWindows()
	{
		return m_topazCLILocationWindows;
	}

	/**
	 * Returns the value of the topazCLILocationLinux field. Used for databinding.
	 * 
	 * @return CLI location - Linux
	 */
	public String getTopazCLILocationLinux()
	{
		return m_topazCLILocationLinux;
	}

	/**
	 * Sets the Topaz CLI installation location for Windows systems.
	 * 
	 * @param location
	 *            the install directory
	 */
	public void setTopazCLILocationWindows(String location)
	{
		m_topazCLILocationWindows = location;
	}

	/**
	 * Sets the Topaz CLI installation location for Linux systems.
	 * 
	 * @param location
	 *            the install directory
	 */
	public void setTopazCLILocationLinux(String location) {
		m_topazCLILocationLinux = location;
	}
	
	/**
	 * Retrieves login information given a credentials identifier.
	 * 
	 * @param project
	 *            the Jenkins project
	 * @param credentialsId
	 *            the <code>String</code> identifier of the credentials to obtain
	 * @return StandardCredentials object contains the credentials with login information
	 * 			this object can be of type UsernamePasswordCredentialsImpl or CertificateCredentialsImpl.
	 */
	public StandardCredentials getLoginCredentials(Item project, String credentialsId) {
		StandardCredentials credentials = null;

		List<StandardCredentials> credentialsList = CredentialsProvider.lookupCredentials(StandardCredentials.class,
				project, ACL.SYSTEM, Collections.<DomainRequirement>emptyList());

		IdMatcher matcher = new IdMatcher(credentialsId);
		for (StandardCredentials c : credentialsList) {
			if (matcher.matches(c)) {
				credentials = c;
			}
		}

		return credentials;
	}
	
	/**
	 * Get the user identified by the given credentials.
	 * 
	 * @param credentials
	 *            a Jenkins <code>StandardCredentials</code> instance
	 * 
	 * @return String containing the user identified by the credentials
	 * @throws AbortException
	 *             if there is a problem with the credentials
	 */
	public String getCredentialsUser(StandardCredentials credentials) throws AbortException {
		String user;
		if (credentials instanceof StandardUsernamePasswordCredentials) {
			user = ((StandardUsernamePasswordCredentials) credentials).getUsername();
		} else if (credentials instanceof StandardCertificateCredentials) {
			StandardCertificateCredentials credentialsCer = (StandardCertificateCredentials) credentials;
			try {
				user = getSubjectDN(getX509Certificate(credentialsCer));
			} catch (KeyStoreException e) {
				throw new AbortException(String.format("Unable to obtain user name from certificate: %s", e.getMessage())); //$NON-NLS-1$
			}
		} else {
			throw new AbortException("Unsupported credentials type!"); //$NON-NLS-1$
		}
		
		return user;
	}

	/**
	 * Get the subject distinguished name from the specified certificate.
	 * 
	 * @param x509Certificate
	 *            The instance of <code>X509Certificate</code> from which the subject distinguished name is to be returned.
	 * 
	 * @return The subject distinguished name or <code>null</code> if the name could not be retrieved
	 */
	public String getSubjectDN(X509Certificate x509Certificate) {
		String subject = StringUtils.EMPTY;

		if (x509Certificate != null) {
			Principal subjectPrincipal = x509Certificate.getSubjectDN();
			if (subjectPrincipal != null) {
				subject = subjectPrincipal.getName();
			}
		}

		return subject;
	}
	
	/**
	 * @param credentials An instance of
	 *                    <code>StandardCertificateCredentials</code>.
	 *                    
	 * @return a string of Base64 encoded representation of the <code>X509Certificate</code>.
	 * 			if we found this certificate in certificateChain using the keystore
	 *         <code>null</code> is return if no <code>X509Certificate</code>
	 *         founded.
	 *         
	 * @throws KeyStoreException ~ unable to find an <code>X509Certificate</code> from a <code>StandardCertificateCredentials</code>
	 */
	public X509Certificate getX509Certificate(StandardCertificateCredentials credentials) throws KeyStoreException {
		Certificate certificate = null;
		X509Certificate x509 = null;

		KeyStore keyStoreFromSystem = credentials.getKeyStore();
		for (Enumeration<String> enumeration = keyStoreFromSystem.aliases(); enumeration.hasMoreElements();) {
			String alias = enumeration.nextElement();
			certificate = keyStoreFromSystem.getCertificate(alias);
			if (certificate instanceof X509Certificate) {
				x509 = (X509Certificate) certificate;
			}
		}

		return x509;
	}
	
	/**
	 * @param credentials An instance of
	 *                    <code>StandardCertificateCredentials</code>.
	 *                    
	 * @return a string of Base64 encoded representation of the <code>X509Certificate</code>.
	 * 			if we found this certificate in certificateChain using the keystore
	 *         <code>null</code> is return if no <code>X509Certificate</code>
	 *         founded.
	 *         
	 * @throws KeyStoreException ~ unable to create an encoded certifcate string from a <code>StandardCertificateCredentials</code>
	 * @throws CertificateEncodingException ~ unable to encode a certifcate obtained from a <code>StandardCertificateCredentials</code>
	 */
	public String getCertificateString(StandardCertificateCredentials credentials) throws KeyStoreException, CertificateEncodingException {
		
		String certString = null;
		X509Certificate x509 = getX509Certificate(credentials);
		
		// Encode the X509Certificate as a Base64 string.
		if (x509 != null) {
			byte[] encodedCert = x509.getEncoded();
			certString = new String(Base64.encodeBase64(encodedCert), StandardCharsets.UTF_8);
		}
		return certString;
	}

	/**
	 * Get an argument list builder for the common arguments.
	 * 
	 * @param scriptFileArg
	 *            the name of the application script used to start the CLI
	 * @param cliVersion
	 *            the CLI version for testing compatibility
	 * @param project
	 *            the jenkins project
	 * @param credentialsId
	 *            the identifier of credentials to use
	 * @param connectionId
	 *            the identifier of the host connection to use
	 * 
	 * @return a new argument list builder initialized with common arguments
	 * @throws AbortException
	 *             if unable to initialize the argument builder
	 */
	public ArgumentListBuilder getArgumentBuilder(String scriptFileArg, String cliVersion, Item project, String credentialsId,
			String connectionId) throws AbortException {
		ArgumentListBuilder args = new ArgumentListBuilder();
		args.add(scriptFileArg);

		HostConnection connection = getHostConnection(connectionId);
		addConnectionArgs(args, connection, cliVersion);

		StandardCredentials credentials = getLoginCredentials(project, credentialsId);
		addCredentialsArgs(args, credentials);

		return args;
	}

	/**
	 * Adds the connection arguments.
	 * 
	 * @param args
	 *            the argument list builder to add the connection details to
	 * @param connection
	 *            the connection to add
	 * @param cliVersion
	 *            the CLI version
	 * @throws AbortException
	 *             if the encryption protocol is not supported
	 */
	private void addConnectionArgs(ArgumentListBuilder args, HostConnection connection, String cliVersion) throws AbortException {
		args.add(CommonConstants.HOST_PARM, ArgumentUtils.escapeForScript(connection.getHost()));
		args.add(CommonConstants.PORT_PARM, ArgumentUtils.escapeForScript(connection.getPort()));

		// do not pass protocol on command line if null, empty, blank, or 'None'
		String protocol = connection.getProtocol();
		if (StringUtils.isNotBlank(protocol) && !StringUtils.equalsIgnoreCase(protocol, "none")) { //$NON-NLS-1$
			CLIVersionUtils.checkProtocolSupported(cliVersion);
			args.add(CommonConstants.PROTOCOL_PARM, protocol);
		}

		args.add(CommonConstants.CODE_PAGE_PARM, connection.getCodePage());
		args.add(CommonConstants.TIMEOUT_PARM, ArgumentUtils.escapeForScript(connection.getTimeout()));
	}

	/**
	 * Adds the credentials arguments.
	 * 
	 * @param args
	 *            the argument list builder to add the credentials to
	 * @param credentials
	 *            the credentials to add
	 * @throws AbortException
	 *             if there is a problem with the credentials
	 */
	private void addCredentialsArgs(ArgumentListBuilder args, StandardCredentials credentials) throws AbortException {
		if (credentials instanceof StandardUsernamePasswordCredentials) {
			addUsernamePasswordCredentialsArgs(args, (StandardUsernamePasswordCredentials) credentials);
		} else if (credentials instanceof StandardCertificateCredentials) {
			addCertificateCredentialsArgs(args, (StandardCertificateCredentials) credentials);
		} else {
			throw new AbortException("Unsupported credentials type!"); //$NON-NLS-1$
		}
	}

	/**
	 * Adds the certificate credentials arguments.
	 * 
	 * @param args
	 *            the argument list builder to add the credentials to
	 * @param credentials
	 *            the certificate credentials to add
	 * @throws AbortException
	 *             if there is a problem with the credentials
	 */
	private void addCertificateCredentialsArgs(ArgumentListBuilder args, StandardCertificateCredentials credentials) throws AbortException {
		// Don't add userid for now.  This comes out as a long string and causes problems with the CLI argument parser.
		try {
			// 2021-06-01 Escaped cert fails. so pass it as is.
			String certificateStr = getCertificateString(credentials);
			if (certificateStr != null) {
				args.add(CommonConstants.CERT_PARM, certificateStr);
			}
		} catch (KeyStoreException | CertificateEncodingException e) {
			throw new AbortException(String.format("Unable to add certificate credentials: %s", e.getMessage())); //$NON-NLS-1$
		}
	}

	/**
	 * Adds the username and password credentials arguments.
	 * 
	 * @param args
	 *            the argument list builder to add the credentials to
	 * @param credentials
	 *            the username and password credentials to add
	 */
	private void addUsernamePasswordCredentialsArgs(ArgumentListBuilder args, StandardUsernamePasswordCredentials credentials) {
		String userId = ArgumentUtils.escapeForScript(credentials.getUsername());
		args.add(CommonConstants.USERID_PARM, userId);

		String password = ArgumentUtils.escapeForScript(credentials.getPassword().getPlainText());
		args.add(CommonConstants.PW_PARM);
		args.add(password, true);
	}
}
