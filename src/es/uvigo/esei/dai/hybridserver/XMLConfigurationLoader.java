/**
 *  HybridServer
 *  Copyright (C) 2017 Miguel Reboiro-Jato
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XMLConfigurationLoader {

	private int httpPort, numClients;
	private String webServiceURL, userdb, passworddb, dbURL;
	private List<ServerConfiguration> servers;

	public Configuration load(File xmlFile) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		Document tree = builder.parse(xmlFile);
		tree.normalize();

		NodeList nodelist = tree.getElementsByTagName("configuration");
		Node configurationNode = nodelist.item(0);
		NodeList items = configurationNode.getChildNodes();

		int numPartsConfiguration = 0;
		for (int i = 0; i < items.getLength(); i++) {
			if (items.item(i).getNodeType() == Node.ELEMENT_NODE) {
				switch (items.item(i).getNodeName()) {
					case "connections":
						loadConnections(items.item(i));
						numPartsConfiguration++;
						break;
					case "database":
						loadDatabase(items.item(i));
						numPartsConfiguration++;
						break;
					case "servers":
						loadServers(items.item(i));
						numPartsConfiguration++;
						break;
				}

			}
		}

		if (numPartsConfiguration == 3) {
			Configuration config = new Configuration(this.httpPort, this.numClients, this.webServiceURL, this.userdb,
					this.passworddb, this.dbURL, this.servers);
			return config;

		} else
			throw new Exception();
	}

	private void loadConnections(Node node) throws Exception {
		int numParameterConnection = 0;
		NodeList connectionParameters = node.getChildNodes();
		for (int i = 0; i < connectionParameters.getLength(); i++) {
			Node parameter = connectionParameters.item(i);
			if (parameter.getNodeType() == Node.ELEMENT_NODE) {
				switch (parameter.getNodeName()) {
					case "http":
						this.httpPort = Integer.parseInt(parameter.getTextContent());
						numParameterConnection++;
						break;
					case "webservice":
						this.webServiceURL = parameter.getTextContent();
						numParameterConnection++;
						break;
					case "numClients":
						this.numClients = Integer.parseInt(parameter.getTextContent());
						numParameterConnection++;
						break;
				}
			}
		}
		if (numParameterConnection != 3)
			throw new Exception();
	}

	private void loadDatabase(Node node) throws Exception {
		int numParametersDB = 0;
		NodeList databaseParameters = node.getChildNodes();
		for (int i = 0; i < databaseParameters.getLength(); i++) {
			Node parameter = databaseParameters.item(i);
			if (parameter.getNodeType() == Node.ELEMENT_NODE) {
				switch (parameter.getNodeName()) {
					case "user":
						this.userdb = parameter.getTextContent();
						numParametersDB++;
						break;
					case "password":
						this.passworddb = parameter.getTextContent();
						numParametersDB++;
						break;
					case "url":
						this.dbURL = parameter.getTextContent();
						numParametersDB++;
						break;
				}
			}
		}
		if (numParametersDB != 3)
			throw new Exception();
	}

	private void loadServers(Node node) throws Exception {
		int numParametersPerServer = 0;
		this.servers = new ArrayList<>();
		NodeList serversParameters = node.getChildNodes();
		for (int i = 0; i < serversParameters.getLength(); i++) {
			Node server = serversParameters.item(i);
			if (server.getNodeType() == Node.ELEMENT_NODE) {
				NamedNodeMap attributes = server.getAttributes();
				ServerConfiguration serverConfiguration = new ServerConfiguration();

				for (int j = 0; j < attributes.getLength(); j++) {
					Node attribute = attributes.item(j);
					if (attribute.getNodeType() == Node.ATTRIBUTE_NODE) {

						switch (attribute.getNodeName()) {
							case "name":
								serverConfiguration.setName(attribute.getNodeValue());
								numParametersPerServer++;
								break;
							case "wsdl":
								serverConfiguration.setWsdl(attribute.getNodeValue());
								numParametersPerServer++;
								break;
							case "namespace":
								serverConfiguration.setNamespace(attribute.getNodeValue());
								numParametersPerServer++;
								break;
							case "service":
								serverConfiguration.setService(attribute.getNodeValue());
								numParametersPerServer++;
								break;
							case "httpAddress":
								serverConfiguration.setHttpAddress(attribute.getNodeValue());
								numParametersPerServer++;
								break;
						}
					}
				}

				if (numParametersPerServer == 5) {
					this.servers.add(serverConfiguration);
					numParametersPerServer = 0;
				} else
					throw new Exception();
			}
		}
	}

}
