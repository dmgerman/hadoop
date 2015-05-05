begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|Class
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * Base class for comparing fields in one or more Configuration classes  * against a corresponding .xml file.  Usage is intended as follows:  *<p></p>  *<ol>  *<li> Create a subclass to TestConfigurationFieldsBase  *<li> Define<code>initializeMemberVariables</code> method in the  *      subclass.  In this class, do the following:  *<p></p>  *<ol>  *<li><b>Required</b> Set the variable<code>xmlFilename</code> to  *        the appropriate xml definition file  *<li><b>Required</b> Set the variable<code>configurationClasses</code>  *        to an array of the classes which define the constants used by the  *        code corresponding to the xml files  *<li><b>Optional</b> Set<code>errorIfMissingConfigProps</code> if the  *        subclass should throw an error in the method  *<code>testCompareXmlAgainstConfigurationClass</code>  *<li><b>Optional</b> Set<code>errorIfMissingXmlProps</code> if the  *        subclass should throw an error in the method  *<code>testCompareConfigurationClassAgainstXml</code>  *<li><b>Optional</b> Instantiate and populate strings into one or  *        more of the following variables:  *<br><code>configurationPropsToSkipCompare</code>  *<br><code>configurationPrefixToSkipCompare</code>  *<br><code>xmlPropsToSkipCompare</code>  *<br><code>xmlPrefixToSkipCompare</code>  *<br>  *        in order to get comparisons clean  *</ol>  *</ol>  *<p></p>  * The tests to do class-to-file and file-to-class should automatically  * run.  This class (and its subclasses) are mostly not intended to be  * overridden, but to do a very specific form of comparison testing.  */
end_comment

begin_class
annotation|@
name|Ignore
DECL|class|TestConfigurationFieldsBase
specifier|public
specifier|abstract
class|class
name|TestConfigurationFieldsBase
block|{
comment|/**    * Member variable for storing xml filename.    */
DECL|field|xmlFilename
specifier|protected
name|String
name|xmlFilename
init|=
literal|null
decl_stmt|;
comment|/**    * Member variable for storing all related Configuration classes.    */
DECL|field|configurationClasses
specifier|protected
name|Class
index|[]
name|configurationClasses
init|=
literal|null
decl_stmt|;
comment|/**    * Throw error during comparison if missing configuration properties.    * Intended to be set by subclass.    */
DECL|field|errorIfMissingConfigProps
specifier|protected
name|boolean
name|errorIfMissingConfigProps
init|=
literal|false
decl_stmt|;
comment|/**    * Throw error during comparison if missing xml properties.  Intended    * to be set by subclass.    */
DECL|field|errorIfMissingXmlProps
specifier|protected
name|boolean
name|errorIfMissingXmlProps
init|=
literal|false
decl_stmt|;
comment|/**    * Set of properties to skip extracting (and thus comparing later) in     * extractMemberVariablesFromConfigurationFields.    */
DECL|field|configurationPropsToSkipCompare
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|configurationPropsToSkipCompare
init|=
literal|null
decl_stmt|;
comment|/**    * Set of property prefixes to skip extracting (and thus comparing later)    * in * extractMemberVariablesFromConfigurationFields.    */
DECL|field|configurationPrefixToSkipCompare
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|configurationPrefixToSkipCompare
init|=
literal|null
decl_stmt|;
comment|/**    * Set of properties to skip extracting (and thus comparing later) in     * extractPropertiesFromXml.    */
DECL|field|xmlPropsToSkipCompare
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|xmlPropsToSkipCompare
init|=
literal|null
decl_stmt|;
comment|/**    * Set of property prefixes to skip extracting (and thus comparing later)    * in extractPropertiesFromXml.    */
DECL|field|xmlPrefixToSkipCompare
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|xmlPrefixToSkipCompare
init|=
literal|null
decl_stmt|;
comment|/**    * Member variable to store Configuration variables for later comparison.    */
DECL|field|configurationMemberVariables
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|configurationMemberVariables
init|=
literal|null
decl_stmt|;
comment|/**    * Member variable to store XML properties for later comparison.    */
DECL|field|xmlKeyValueMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|xmlKeyValueMap
init|=
literal|null
decl_stmt|;
comment|/**    * Member variable to store Configuration variables that are not in the    * corresponding XML file.    */
DECL|field|configurationFieldsMissingInXmlFile
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|configurationFieldsMissingInXmlFile
init|=
literal|null
decl_stmt|;
comment|/**    * Member variable to store XML variables that are not in the    * corresponding Configuration class(es).    */
DECL|field|xmlFieldsMissingInConfiguration
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|xmlFieldsMissingInConfiguration
init|=
literal|null
decl_stmt|;
comment|/**    * Member variable for debugging base class operation    */
DECL|field|configDebug
specifier|protected
name|boolean
name|configDebug
init|=
literal|false
decl_stmt|;
DECL|field|xmlDebug
specifier|protected
name|boolean
name|xmlDebug
init|=
literal|false
decl_stmt|;
comment|/**    * Abstract method to be used by subclasses for initializing base    * members.    */
DECL|method|initializeMemberVariables ()
specifier|public
specifier|abstract
name|void
name|initializeMemberVariables
parameter_list|()
function_decl|;
comment|/**    * Utility function to extract&quot;public static final&quot; member    * variables from a Configuration type class.    *    * @param fields The class member variables    * @return HashMap containing<StringValue,MemberVariableName> entries    */
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
DECL|method|extractMemberVariablesFromConfigurationFields (Field[] fields)
name|extractMemberVariablesFromConfigurationFields
parameter_list|(
name|Field
index|[]
name|fields
parameter_list|)
block|{
comment|// Sanity Check
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|retVal
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// Setup regexp for valid properties
name|String
name|propRegex
init|=
literal|"^[A-Za-z][A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)+$"
decl_stmt|;
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|propRegex
argument_list|)
decl_stmt|;
comment|// Iterate through class member variables
name|int
name|totalFields
init|=
literal|0
decl_stmt|;
name|String
name|value
decl_stmt|;
for|for
control|(
name|Field
name|f
range|:
name|fields
control|)
block|{
if|if
condition|(
name|configDebug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Field: "
operator|+
name|f
argument_list|)
expr_stmt|;
block|}
comment|// Filter out anything that isn't "public static final"
if|if
condition|(
operator|!
name|Modifier
operator|.
name|isStatic
argument_list|(
name|f
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|||
operator|!
name|Modifier
operator|.
name|isPublic
argument_list|(
name|f
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|||
operator|!
name|Modifier
operator|.
name|isFinal
argument_list|(
name|f
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// Filter out anything that isn't a string.  int/float are generally
comment|// default values
if|if
condition|(
operator|!
name|f
operator|.
name|getType
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"java.lang.String"
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// Convert found member into String
try|try
block|{
name|value
operator|=
operator|(
name|String
operator|)
name|f
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|iaException
parameter_list|)
block|{
continue|continue;
block|}
if|if
condition|(
name|configDebug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  Value: "
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
comment|// Special Case: Detect and ignore partial properties (ending in x)
comment|//               or file properties (ending in .xml)
if|if
condition|(
name|value
operator|.
name|endsWith
argument_list|(
literal|".xml"
argument_list|)
operator|||
name|value
operator|.
name|endsWith
argument_list|(
literal|"."
argument_list|)
operator|||
name|value
operator|.
name|endsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
continue|continue;
comment|// Ignore known configuration props
if|if
condition|(
name|configurationPropsToSkipCompare
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|configurationPropsToSkipCompare
operator|.
name|contains
argument_list|(
name|value
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
comment|// Ignore known configuration prefixes
name|boolean
name|skipPrefix
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|configurationPrefixToSkipCompare
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|cfgPrefix
range|:
name|configurationPrefixToSkipCompare
control|)
block|{
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
name|cfgPrefix
argument_list|)
condition|)
block|{
name|skipPrefix
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|skipPrefix
condition|)
block|{
continue|continue;
block|}
comment|// Positive Filter: Look only for property values.  Expect it to look
comment|//                  something like: blah.blah2(.blah3.blah4...)
name|Matcher
name|m
init|=
name|p
operator|.
name|matcher
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
if|if
condition|(
name|configDebug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  Passes Regex: false"
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
if|if
condition|(
name|configDebug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  Passes Regex: true"
argument_list|)
expr_stmt|;
block|}
comment|// Save member variable/value as hash
if|if
condition|(
operator|!
name|retVal
operator|.
name|containsKey
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|retVal
operator|.
name|put
argument_list|(
name|value
argument_list|,
name|f
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|configDebug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ERROR: Already found key for property "
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|retVal
return|;
block|}
comment|/**    * Pull properties and values from filename.    *    * @param filename XML filename    * @return HashMap containing<Property,Value> entries from XML file    */
DECL|method|extractPropertiesFromXml (String filename)
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|extractPropertiesFromXml
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
if|if
condition|(
name|filename
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Iterate through XML file for name/value pairs
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setAllowNullValueProperties
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|addResource
argument_list|(
name|filename
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|retVal
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|kvItr
init|=
name|conf
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|kvItr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
init|=
name|kvItr
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
comment|// Ignore known xml props
if|if
condition|(
name|xmlPropsToSkipCompare
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|xmlPropsToSkipCompare
operator|.
name|contains
argument_list|(
name|key
argument_list|)
condition|)
block|{
if|if
condition|(
name|xmlDebug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  Skipping Full Key: "
operator|+
name|key
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
block|}
comment|// Ignore known xml prefixes
name|boolean
name|skipPrefix
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|xmlPrefixToSkipCompare
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|xmlPrefix
range|:
name|xmlPrefixToSkipCompare
control|)
block|{
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|xmlPrefix
argument_list|)
condition|)
block|{
name|skipPrefix
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|skipPrefix
condition|)
block|{
if|if
condition|(
name|xmlDebug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  Skipping Prefix Key: "
operator|+
name|key
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
if|if
condition|(
name|conf
operator|.
name|onlyKeyExists
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|retVal
operator|.
name|put
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|xmlDebug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  XML Key,Null Value: "
operator|+
name|key
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|String
name|value
init|=
name|conf
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|retVal
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|xmlDebug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  XML Key,Valid Value: "
operator|+
name|key
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|kvItr
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
return|return
name|retVal
return|;
block|}
comment|/**    * Perform set difference operation on keyMap2 from keyMap1.    *    * @param keyMap1 The initial set    * @param keyMap2 The set to subtract    * @return Returns set operation keyMap1-keyMap2    */
DECL|method|compareConfigurationToXmlFields (Map<String,String> keyMap1, Map<String,String> keyMap2)
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|compareConfigurationToXmlFields
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|keyMap1
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|keyMap2
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|retVal
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|keyMap1
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|retVal
operator|.
name|removeAll
argument_list|(
name|keyMap2
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|retVal
return|;
block|}
comment|/**    * Initialize the four variables corresponding the Configuration    * class and the XML properties file.    */
annotation|@
name|Before
DECL|method|setupTestConfigurationFields ()
specifier|public
name|void
name|setupTestConfigurationFields
parameter_list|()
throws|throws
name|Exception
block|{
name|initializeMemberVariables
argument_list|()
expr_stmt|;
comment|// Error if subclass hasn't set class members
name|assertTrue
argument_list|(
name|xmlFilename
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|configurationClasses
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|// Create class member/value map
name|configurationMemberVariables
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|configDebug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Reading configuration classes"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Class
name|c
range|:
name|configurationClasses
control|)
block|{
name|Field
index|[]
name|fields
init|=
name|c
operator|.
name|getDeclaredFields
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|memberMap
init|=
name|extractMemberVariablesFromConfigurationFields
argument_list|(
name|fields
argument_list|)
decl_stmt|;
if|if
condition|(
name|memberMap
operator|!=
literal|null
condition|)
block|{
name|configurationMemberVariables
operator|.
name|putAll
argument_list|(
name|memberMap
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|configDebug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"====="
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
comment|// Create XML key/value map
if|if
condition|(
name|xmlDebug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Reading XML property files"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
name|xmlKeyValueMap
operator|=
name|extractPropertiesFromXml
argument_list|(
name|xmlFilename
argument_list|)
expr_stmt|;
if|if
condition|(
name|xmlDebug
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"====="
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
comment|// Find class members not in the XML file
name|configurationFieldsMissingInXmlFile
operator|=
name|compareConfigurationToXmlFields
argument_list|(
name|configurationMemberVariables
argument_list|,
name|xmlKeyValueMap
argument_list|)
expr_stmt|;
comment|// Find XML properties not in the class
name|xmlFieldsMissingInConfiguration
operator|=
name|compareConfigurationToXmlFields
argument_list|(
name|xmlKeyValueMap
argument_list|,
name|configurationMemberVariables
argument_list|)
expr_stmt|;
block|}
comment|/**    * Compares the properties that are in the Configuration class, but not    * in the XML properties file.    */
annotation|@
name|Test
DECL|method|testCompareConfigurationClassAgainstXml ()
specifier|public
name|void
name|testCompareConfigurationClassAgainstXml
parameter_list|()
block|{
comment|// Error if subclass hasn't set class members
name|assertTrue
argument_list|(
name|xmlFilename
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|configurationClasses
operator|!=
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|int
name|missingXmlSize
init|=
name|configurationFieldsMissingInXmlFile
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|Class
name|c
range|:
name|configurationClasses
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  ("
operator|+
name|configurationMemberVariables
operator|.
name|size
argument_list|()
operator|+
literal|" member variables)"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|StringBuffer
name|xmlErrorMsg
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|Class
name|c
range|:
name|configurationClasses
control|)
block|{
name|xmlErrorMsg
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|xmlErrorMsg
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|xmlErrorMsg
operator|.
name|append
argument_list|(
literal|"has "
argument_list|)
expr_stmt|;
name|xmlErrorMsg
operator|.
name|append
argument_list|(
name|missingXmlSize
argument_list|)
expr_stmt|;
name|xmlErrorMsg
operator|.
name|append
argument_list|(
literal|" variables missing in "
argument_list|)
expr_stmt|;
name|xmlErrorMsg
operator|.
name|append
argument_list|(
name|xmlFilename
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|xmlErrorMsg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
if|if
condition|(
name|missingXmlSize
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  (None)"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|missingField
range|:
name|configurationFieldsMissingInXmlFile
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|missingField
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"====="
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
if|if
condition|(
name|errorIfMissingXmlProps
condition|)
block|{
name|assertTrue
argument_list|(
name|xmlErrorMsg
operator|.
name|toString
argument_list|()
argument_list|,
name|missingXmlSize
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Compares the properties that are in the XML properties file, but not    * in the Configuration class.    */
annotation|@
name|Test
DECL|method|testCompareXmlAgainstConfigurationClass ()
specifier|public
name|void
name|testCompareXmlAgainstConfigurationClass
parameter_list|()
block|{
comment|// Error if subclass hasn't set class members
name|assertTrue
argument_list|(
name|xmlFilename
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|configurationClasses
operator|!=
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|int
name|missingConfigSize
init|=
name|xmlFieldsMissingInConfiguration
operator|.
name|size
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"File "
operator|+
name|xmlFilename
operator|+
literal|" ("
operator|+
name|xmlKeyValueMap
operator|.
name|size
argument_list|()
operator|+
literal|" properties)"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|StringBuffer
name|configErrorMsg
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|configErrorMsg
operator|.
name|append
argument_list|(
name|xmlFilename
argument_list|)
expr_stmt|;
name|configErrorMsg
operator|.
name|append
argument_list|(
literal|" has "
argument_list|)
expr_stmt|;
name|configErrorMsg
operator|.
name|append
argument_list|(
name|missingConfigSize
argument_list|)
expr_stmt|;
name|configErrorMsg
operator|.
name|append
argument_list|(
literal|" properties missing in"
argument_list|)
expr_stmt|;
for|for
control|(
name|Class
name|c
range|:
name|configurationClasses
control|)
block|{
name|configErrorMsg
operator|.
name|append
argument_list|(
literal|"  "
operator|+
name|c
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|configErrorMsg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
if|if
condition|(
name|missingConfigSize
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  (None)"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|missingField
range|:
name|xmlFieldsMissingInConfiguration
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|missingField
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"====="
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
if|if
condition|(
name|errorIfMissingConfigProps
condition|)
block|{
name|assertTrue
argument_list|(
name|configErrorMsg
operator|.
name|toString
argument_list|()
argument_list|,
name|missingConfigSize
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

