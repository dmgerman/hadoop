begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.security
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|security
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Class keeping code security information  */
end_comment

begin_class
DECL|class|SecurityConfiguration
specifier|public
class|class
name|SecurityConfiguration
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SecurityConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|clusterName
specifier|private
name|String
name|clusterName
decl_stmt|;
comment|//  private void validate() throws SliderException {
comment|//    if (isSecurityEnabled()) {
comment|//      String principal = instanceDefinition.getAppConfOperations()
comment|//          .getComponent(SliderKeys.COMPONENT_AM).get(SliderXmlConfKeys.KEY_KEYTAB_PRINCIPAL);
comment|//      if(SliderUtils.isUnset(principal)) {
comment|//        // if no login identity is available, fail
comment|//        UserGroupInformation loginUser = null;
comment|//        try {
comment|//          loginUser = getLoginUser();
comment|//        } catch (IOException e) {
comment|//          throw new SliderException(EXIT_UNAUTHORIZED, e,
comment|//                                    "No principal configured for the application and "
comment|//                                    + "exception raised during retrieval of login user. "
comment|//                                    + "Unable to proceed with application "
comment|//                                    + "initialization.  Please ensure a value "
comment|//                                    + "for %s exists in the application "
comment|//                                    + "configuration or the login issue is addressed",
comment|//                                    SliderXmlConfKeys.KEY_KEYTAB_PRINCIPAL);
comment|//        }
comment|//        if (loginUser == null) {
comment|//          throw new SliderException(EXIT_UNAUTHORIZED,
comment|//                                    "No principal configured for the application "
comment|//                                    + "and no login user found. "
comment|//                                    + "Unable to proceed with application "
comment|//                                    + "initialization.  Please ensure a value "
comment|//                                    + "for %s exists in the application "
comment|//                                    + "configuration or the login issue is addressed",
comment|//                                    SliderXmlConfKeys.KEY_KEYTAB_PRINCIPAL);
comment|//        }
comment|//      }
comment|//      // ensure that either local or distributed keytab mechanism is enabled,
comment|//      // but not both
comment|//      String keytabFullPath = instanceDefinition.getAppConfOperations()
comment|//          .getComponent(SliderKeys.COMPONENT_AM)
comment|//          .get(SliderXmlConfKeys.KEY_AM_KEYTAB_LOCAL_PATH);
comment|//      String keytabName = instanceDefinition.getAppConfOperations()
comment|//          .getComponent(SliderKeys.COMPONENT_AM)
comment|//          .get(SliderXmlConfKeys.KEY_AM_LOGIN_KEYTAB_NAME);
comment|//      if (SliderUtils.isSet(keytabFullPath)&& SliderUtils.isSet(keytabName)) {
comment|//        throw new SliderException(EXIT_UNAUTHORIZED,
comment|//                                  "Both a keytab on the cluster host (%s) and a"
comment|//                                  + " keytab to be retrieved from HDFS (%s) are"
comment|//                                  + " specified.  Please configure only one keytab"
comment|//                                  + " retrieval mechanism.",
comment|//                                  SliderXmlConfKeys.KEY_AM_KEYTAB_LOCAL_PATH,
comment|//                                  SliderXmlConfKeys.KEY_AM_LOGIN_KEYTAB_NAME);
comment|//
comment|//      }
comment|//    }
comment|//  }
comment|//
comment|//  protected UserGroupInformation getLoginUser() throws IOException {
comment|//    return UserGroupInformation.getLoginUser();
comment|//  }
comment|//
comment|//  public boolean isSecurityEnabled () {
comment|//    return SliderUtils.isHadoopClusterSecure(configuration);
comment|//  }
comment|//
comment|//  public String getPrincipal () throws IOException {
comment|//    String principal = instanceDefinition.getAppConfOperations()
comment|//        .getComponent(SliderKeys.COMPONENT_AM).get(SliderXmlConfKeys.KEY_KEYTAB_PRINCIPAL);
comment|//    if (SliderUtils.isUnset(principal)) {
comment|//      principal = UserGroupInformation.getLoginUser().getShortUserName();
comment|//      log.info("No principal set in the slider configuration.  Will use AM login"
comment|//               + " identity {} to attempt keytab-based login", principal);
comment|//    }
comment|//
comment|//    return principal;
comment|//  }
comment|//
comment|//  public boolean isKeytabProvided() {
comment|//    boolean keytabProvided = instanceDefinition.getAppConfOperations()
comment|//                    .getComponent(SliderKeys.COMPONENT_AM)
comment|//                    .get(SliderXmlConfKeys.KEY_AM_KEYTAB_LOCAL_PATH) != null ||
comment|//                instanceDefinition.getAppConfOperations()
comment|//                    .getComponent(SliderKeys.COMPONENT_AM).
comment|//                    get(SliderXmlConfKeys.KEY_AM_LOGIN_KEYTAB_NAME) != null;
comment|//    return keytabProvided;
comment|//
comment|//  }
comment|//
comment|//  public File getKeytabFile(AggregateConf instanceDefinition)
comment|//      throws SliderException, IOException {
comment|//    //TODO implement this for dash semantic
comment|//    String keytabFullPath = instanceDefinition.getAppConfOperations()
comment|//        .getComponent(SliderKeys.COMPONENT_AM)
comment|//        .get(SliderXmlConfKeys.KEY_AM_KEYTAB_LOCAL_PATH);
comment|//    File localKeytabFile;
comment|//    if (SliderUtils.isUnset(keytabFullPath)) {
comment|//      // get the keytab
comment|//      String keytabName = instanceDefinition.getAppConfOperations()
comment|//          .getComponent(SliderKeys.COMPONENT_AM).
comment|//              get(SliderXmlConfKeys.KEY_AM_LOGIN_KEYTAB_NAME);
comment|//      log.info("No host keytab file path specified. Will attempt to retrieve"
comment|//               + " keytab file {} as a local resource for the container",
comment|//               keytabName);
comment|//      // download keytab to local, protected directory
comment|//      localKeytabFile = new File(SliderKeys.KEYTAB_DIR, keytabName);
comment|//    } else {
comment|//      log.info("Using host keytab file {} for login", keytabFullPath);
comment|//      localKeytabFile = new File(keytabFullPath);
comment|//    }
comment|//    return localKeytabFile;
comment|//  }
block|}
end_class

end_unit

