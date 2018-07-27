begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.exceptions
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|exceptions
package|;
end_package

begin_interface
DECL|interface|RestApiErrorMessages
specifier|public
interface|interface
name|RestApiErrorMessages
block|{
DECL|field|ERROR_APPLICATION_NAME_INVALID
name|String
name|ERROR_APPLICATION_NAME_INVALID
init|=
literal|"Service name is either empty or not provided"
decl_stmt|;
DECL|field|ERROR_APPLICATION_VERSION_INVALID
name|String
name|ERROR_APPLICATION_VERSION_INVALID
init|=
literal|"Version of service %s is either empty or not provided"
decl_stmt|;
DECL|field|ERROR_APPLICATION_NAME_INVALID_FORMAT
name|String
name|ERROR_APPLICATION_NAME_INVALID_FORMAT
init|=
literal|"Service name %s is not valid - only lower case letters, digits, "
operator|+
literal|"and hyphen are allowed, and the name must be no more "
operator|+
literal|"than 63 characters"
decl_stmt|;
DECL|field|ERROR_COMPONENT_NAME_INVALID
name|String
name|ERROR_COMPONENT_NAME_INVALID
init|=
literal|"Component name must be no more than %s characters: %s"
decl_stmt|;
DECL|field|ERROR_COMPONENT_NAME_CONFLICTS_WITH_SERVICE_NAME
name|String
name|ERROR_COMPONENT_NAME_CONFLICTS_WITH_SERVICE_NAME
init|=
literal|"Component name %s must not be same as service name %s"
decl_stmt|;
DECL|field|ERROR_USER_NAME_INVALID
name|String
name|ERROR_USER_NAME_INVALID
init|=
literal|"User name must be no more than 63 characters"
decl_stmt|;
DECL|field|ERROR_APPLICATION_NOT_RUNNING
name|String
name|ERROR_APPLICATION_NOT_RUNNING
init|=
literal|"Service not running"
decl_stmt|;
DECL|field|ERROR_APPLICATION_DOES_NOT_EXIST
name|String
name|ERROR_APPLICATION_DOES_NOT_EXIST
init|=
literal|"Service not found"
decl_stmt|;
DECL|field|ERROR_APPLICATION_IN_USE
name|String
name|ERROR_APPLICATION_IN_USE
init|=
literal|"Service already exists in started"
operator|+
literal|" state"
decl_stmt|;
DECL|field|ERROR_APPLICATION_INSTANCE_EXISTS
name|String
name|ERROR_APPLICATION_INSTANCE_EXISTS
init|=
literal|"Service already exists in"
operator|+
literal|" stopped/failed state (either restart with PUT or destroy with DELETE"
operator|+
literal|" before creating a new one)"
decl_stmt|;
DECL|field|ERROR_SUFFIX_FOR_COMPONENT
name|String
name|ERROR_SUFFIX_FOR_COMPONENT
init|=
literal|" for component %s (nor at the global level)"
decl_stmt|;
DECL|field|ERROR_ARTIFACT_INVALID
name|String
name|ERROR_ARTIFACT_INVALID
init|=
literal|"Artifact is not provided"
decl_stmt|;
DECL|field|ERROR_ARTIFACT_FOR_COMP_INVALID
name|String
name|ERROR_ARTIFACT_FOR_COMP_INVALID
init|=
name|ERROR_ARTIFACT_INVALID
operator|+
name|ERROR_SUFFIX_FOR_COMPONENT
decl_stmt|;
DECL|field|ERROR_ARTIFACT_ID_INVALID
name|String
name|ERROR_ARTIFACT_ID_INVALID
init|=
literal|"Artifact id (like docker image name) is either empty or not provided"
decl_stmt|;
DECL|field|ERROR_ARTIFACT_ID_FOR_COMP_INVALID
name|String
name|ERROR_ARTIFACT_ID_FOR_COMP_INVALID
init|=
name|ERROR_ARTIFACT_ID_INVALID
operator|+
name|ERROR_SUFFIX_FOR_COMPONENT
decl_stmt|;
DECL|field|ERROR_ARTIFACT_PATH_FOR_COMP_INVALID
name|String
name|ERROR_ARTIFACT_PATH_FOR_COMP_INVALID
init|=
literal|"For component %s with %s "
operator|+
literal|"artifact, path does not exist: %s"
decl_stmt|;
DECL|field|ERROR_CONFIGFILE_DEST_FILE_FOR_COMP_NOT_ABSOLUTE
name|String
name|ERROR_CONFIGFILE_DEST_FILE_FOR_COMP_NOT_ABSOLUTE
init|=
literal|"For component %s "
operator|+
literal|"with %s artifact, dest_file must be a relative path: %s"
decl_stmt|;
DECL|field|ERROR_RESOURCE_INVALID
name|String
name|ERROR_RESOURCE_INVALID
init|=
literal|"Resource is not provided"
decl_stmt|;
DECL|field|ERROR_RESOURCE_FOR_COMP_INVALID
name|String
name|ERROR_RESOURCE_FOR_COMP_INVALID
init|=
name|ERROR_RESOURCE_INVALID
operator|+
name|ERROR_SUFFIX_FOR_COMPONENT
decl_stmt|;
DECL|field|ERROR_RESOURCE_MEMORY_INVALID
name|String
name|ERROR_RESOURCE_MEMORY_INVALID
init|=
literal|"Service resource or memory not provided"
decl_stmt|;
DECL|field|ERROR_RESOURCE_CPUS_INVALID
name|String
name|ERROR_RESOURCE_CPUS_INVALID
init|=
literal|"Service resource or cpus not provided"
decl_stmt|;
DECL|field|ERROR_RESOURCE_CPUS_INVALID_RANGE
name|String
name|ERROR_RESOURCE_CPUS_INVALID_RANGE
init|=
literal|"Unacceptable no of cpus specified, either zero or negative"
decl_stmt|;
DECL|field|ERROR_RESOURCE_MEMORY_FOR_COMP_INVALID
name|String
name|ERROR_RESOURCE_MEMORY_FOR_COMP_INVALID
init|=
name|ERROR_RESOURCE_MEMORY_INVALID
operator|+
name|ERROR_SUFFIX_FOR_COMPONENT
decl_stmt|;
DECL|field|ERROR_RESOURCE_CPUS_FOR_COMP_INVALID
name|String
name|ERROR_RESOURCE_CPUS_FOR_COMP_INVALID
init|=
name|ERROR_RESOURCE_CPUS_INVALID
operator|+
name|ERROR_SUFFIX_FOR_COMPONENT
decl_stmt|;
DECL|field|ERROR_RESOURCE_CPUS_FOR_COMP_INVALID_RANGE
name|String
name|ERROR_RESOURCE_CPUS_FOR_COMP_INVALID_RANGE
init|=
name|ERROR_RESOURCE_CPUS_INVALID_RANGE
operator|+
literal|" for component %s (or at the global level)"
decl_stmt|;
DECL|field|ERROR_CONTAINERS_COUNT_INVALID
name|String
name|ERROR_CONTAINERS_COUNT_INVALID
init|=
literal|"Invalid no of containers specified"
decl_stmt|;
DECL|field|ERROR_CONTAINERS_COUNT_FOR_COMP_INVALID
name|String
name|ERROR_CONTAINERS_COUNT_FOR_COMP_INVALID
init|=
name|ERROR_CONTAINERS_COUNT_INVALID
operator|+
name|ERROR_SUFFIX_FOR_COMPONENT
decl_stmt|;
DECL|field|ERROR_DEPENDENCY_INVALID
name|String
name|ERROR_DEPENDENCY_INVALID
init|=
literal|"Dependency %s for component %s is "
operator|+
literal|"invalid, does not exist as a component"
decl_stmt|;
DECL|field|ERROR_DEPENDENCY_CYCLE
name|String
name|ERROR_DEPENDENCY_CYCLE
init|=
literal|"Invalid dependencies, a cycle may "
operator|+
literal|"exist: %s"
decl_stmt|;
DECL|field|ERROR_RESOURCE_PROFILE_MULTIPLE_VALUES_NOT_SUPPORTED
name|String
name|ERROR_RESOURCE_PROFILE_MULTIPLE_VALUES_NOT_SUPPORTED
init|=
literal|"Cannot specify"
operator|+
literal|" cpus/memory along with profile"
decl_stmt|;
DECL|field|ERROR_RESOURCE_PROFILE_MULTIPLE_VALUES_FOR_COMP_NOT_SUPPORTED
name|String
name|ERROR_RESOURCE_PROFILE_MULTIPLE_VALUES_FOR_COMP_NOT_SUPPORTED
init|=
name|ERROR_RESOURCE_PROFILE_MULTIPLE_VALUES_NOT_SUPPORTED
operator|+
literal|" for component %s"
decl_stmt|;
DECL|field|ERROR_RESOURCE_PROFILE_NOT_SUPPORTED_YET
name|String
name|ERROR_RESOURCE_PROFILE_NOT_SUPPORTED_YET
init|=
literal|"Resource profile is not "
operator|+
literal|"supported yet. Please specify cpus/memory."
decl_stmt|;
DECL|field|ERROR_NULL_ARTIFACT_ID
name|String
name|ERROR_NULL_ARTIFACT_ID
init|=
literal|"Artifact Id can not be null if artifact type is none"
decl_stmt|;
DECL|field|ERROR_ABSENT_NUM_OF_INSTANCE
name|String
name|ERROR_ABSENT_NUM_OF_INSTANCE
init|=
literal|"Num of instances should appear either globally or per component"
decl_stmt|;
DECL|field|ERROR_ABSENT_LAUNCH_COMMAND
name|String
name|ERROR_ABSENT_LAUNCH_COMMAND
init|=
literal|"launch_command is required when type is not DOCKER"
decl_stmt|;
DECL|field|ERROR_QUICKLINKS_FOR_COMP_INVALID
name|String
name|ERROR_QUICKLINKS_FOR_COMP_INVALID
init|=
literal|"Quicklinks specified at"
operator|+
literal|" component level, needs corresponding values set at service level"
decl_stmt|;
comment|// Note: %sin is not a typo. Constraint name is optional so the error messages
comment|// below handle that scenario by adding a space if name is specified.
DECL|field|ERROR_PLACEMENT_POLICY_CONSTRAINT_TYPE_NULL
name|String
name|ERROR_PLACEMENT_POLICY_CONSTRAINT_TYPE_NULL
init|=
literal|"Type not specified "
operator|+
literal|"for constraint %sin placement policy of component %s."
decl_stmt|;
DECL|field|ERROR_PLACEMENT_POLICY_CONSTRAINT_SCOPE_NULL
name|String
name|ERROR_PLACEMENT_POLICY_CONSTRAINT_SCOPE_NULL
init|=
literal|"Scope not specified "
operator|+
literal|"for constraint %sin placement policy of component %s."
decl_stmt|;
DECL|field|ERROR_PLACEMENT_POLICY_CONSTRAINT_TAGS_NULL
name|String
name|ERROR_PLACEMENT_POLICY_CONSTRAINT_TAGS_NULL
init|=
literal|"Tag(s) not specified "
operator|+
literal|"for constraint %sin placement policy of component %s."
decl_stmt|;
DECL|field|ERROR_PLACEMENT_POLICY_TAG_NAME_NOT_SAME
name|String
name|ERROR_PLACEMENT_POLICY_TAG_NAME_NOT_SAME
init|=
literal|"Invalid target tag %s "
operator|+
literal|"specified in placement policy of component %s. For now, target tags "
operator|+
literal|"support self reference only. Specifying anything other than its "
operator|+
literal|"component name is not supported. Set target tag of component %s to "
operator|+
literal|"%s."
decl_stmt|;
DECL|field|ERROR_PLACEMENT_POLICY_TAG_NAME_INVALID
name|String
name|ERROR_PLACEMENT_POLICY_TAG_NAME_INVALID
init|=
literal|"Invalid target tag %s "
operator|+
literal|"specified in placement policy of component %s. Target tags should be "
operator|+
literal|"a valid component name in the service."
decl_stmt|;
DECL|field|ERROR_PLACEMENT_POLICY_EXPRESSION_ELEMENT_NAME_INVALID
name|String
name|ERROR_PLACEMENT_POLICY_EXPRESSION_ELEMENT_NAME_INVALID
init|=
literal|"Invalid "
operator|+
literal|"expression element name %s specified in placement policy of component "
operator|+
literal|"%s. Expression element names should be a valid constraint name or an "
operator|+
literal|"expression name defined for this component only."
decl_stmt|;
DECL|field|ERROR_KEYTAB_URI_SCHEME_INVALID
name|String
name|ERROR_KEYTAB_URI_SCHEME_INVALID
init|=
literal|"Unsupported keytab URI scheme: %s"
decl_stmt|;
DECL|field|ERROR_KEYTAB_URI_INVALID
name|String
name|ERROR_KEYTAB_URI_INVALID
init|=
literal|"Invalid keytab URI: %s"
decl_stmt|;
DECL|field|ERROR_COMP_INSTANCE_DOES_NOT_NEED_UPGRADE
name|String
name|ERROR_COMP_INSTANCE_DOES_NOT_NEED_UPGRADE
init|=
literal|"The component instance "
operator|+
literal|"(%s) does not need an upgrade."
decl_stmt|;
DECL|field|ERROR_COMP_DOES_NOT_NEED_UPGRADE
name|String
name|ERROR_COMP_DOES_NOT_NEED_UPGRADE
init|=
literal|"The component (%s) does not need"
operator|+
literal|" an upgrade."
decl_stmt|;
DECL|field|ERROR_KERBEROS_PRINCIPAL_NAME_FORMAT
name|String
name|ERROR_KERBEROS_PRINCIPAL_NAME_FORMAT
init|=
literal|"Kerberos principal (%s) does "
operator|+
literal|" not contain a hostname."
decl_stmt|;
DECL|field|ERROR_KERBEROS_PRINCIPAL_MISSING
name|String
name|ERROR_KERBEROS_PRINCIPAL_MISSING
init|=
literal|"Kerberos principal or keytab is"
operator|+
literal|" missing."
decl_stmt|;
block|}
end_interface

end_unit

