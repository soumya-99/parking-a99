import {
  Text,
  View,
  TouchableOpacity,
  ScrollView,
  NativeModules,
} from 'react-native';
import React, {useContext, useEffect, useState} from 'react';

import InputCustom from '../components/InputCustom';
import ContactBottom from '../components/ContactBottom';
import DeviceInfo from 'react-native-device-info';
import MainView from '../components/MainView';
import SignInHeaderLogo from '../components/SignInHeaderLogo';
import icons from '../resources/icons/icons';
import styles from '../styles/styles';
import {AuthContext} from '../context/AuthProvider';
import strings from '../resources/strings/strings';
import {ezetapStorage} from '../storage/appStorage';

const SignInScreen = ({navigation}) => {
  const [username, setUsername] = useState(() => '');
  const [password, setPassword] = useState(() => '');
  const [deviceId, setDeviceId] = useState(() => '');
  const {login} = useContext(AuthContext);
  const {ReceiptPrinter} = NativeModules;

  useEffect(() => {
    const deviceId = DeviceInfo.getUniqueIdSync();
    setDeviceId(deviceId);
  }, []);

  const initializeEzeAPI = () => {
    return new Promise((resolve, reject) => {
      // Build the JSON string if you need to pass it as a parameter.
      // With our new module, the keys are already hard-coded in the module.
      ReceiptPrinter.initializeEzeAPI(result => {
        console.log('Initialization result:', result);
        if (result.includes('successful')) {
          resolve(result);
        } else {
          reject(result);
        }
      });
    });
  };

  const prepareDevice = () => {
    return new Promise((resolve, reject) => {
      ReceiptPrinter.prepareDevice(result => {
        console.log('Prepare device result:', result);
        if (result.includes('successful')) {
          resolve(result);
        } else {
          reject(result);
        }
      });
    });
  };

  const init = async () => {
    try {
      // Initialize the API
      const initResult = await initializeEzeAPI();
      console.log('EzeAPI initialized:', initResult);

      // Prepare the device
      const prepareResult = await prepareDevice();
      console.log('Device prepared:', prepareResult);

      ezetapStorage.set('ezetap-initialization-json', initResult);
      // Optionally, you can store the initialization response if needed.
      // For example: await ezetapStorage.set("ezetap-initialization-json", initResult);
    } catch (error) {
      console.error('Error during initialization:', error);
    }
  };

  useEffect(() => {
    init();
  }, []);

  return (
    <MainView>
      <ScrollView keyboardShouldPersistTaps="handled">
        <SignInHeaderLogo />

        {/* .............gretting msg............... */}
        <Text style={styles.grettingText}>WELCOME TO</Text>

        {/* .......comapny name ........... */}
        <Text style={[styles.company_name, styles.grettingText]}>
          {strings.app_name}
        </Text>

        {/* ...... divider ....... */}
        <View style={styles.divider} />

        {/* ....... helper text */}
        <Text style={[styles.grettingText, styles.helper_text]}>
          {strings.helper_text}
        </Text>

        <Text
          style={{
            ...styles.grettingText,
            ...styles.helper_text,
            fontSize: 20,
            fontWeight: '600',
          }}>
          Your Device ID is : {deviceId || 'N/A'}
        </Text>
        {/* ...... login container ....... */}
        <View style={[styles.login_container, styles.login_container]}>
          <InputCustom
            icon={icons.phone}
            placeholder="Mobile Number"
            value={username}
            onChangeText={setUsername}
            keyboardType="phone-pad"
          />
          <InputCustom
            icon={icons.unlock}
            placeholder={'Password'}
            value={password}
            onChangeText={setPassword}
            keyboardType={'default'}
            secureTextEntry={true}
          />
          {/* ........ sign in button ....... */}
          <TouchableOpacity
            style={styles.sign_in_button}
            onPress={() => {
              console.log('Login...');
              login(username, password, deviceId);
            }}>
            {icons.arrowRight}
          </TouchableOpacity>
        </View>
        <ContactBottom />
      </ScrollView>
    </MainView>
  );
};

export default SignInScreen;
