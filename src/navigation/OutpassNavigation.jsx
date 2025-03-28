import React from 'react';
import {createNativeStackNavigator} from '@react-navigation/native-stack';

import OutpassScreen from '../screens/BottomNavigationScreens/OutpassScreen';
import CreateOutpassScreen from '../screens/OutpassScreens/CreateOutpassScreen';
import PrintTemplateScreen from '../screens/A99Printer/PrintTemplateScreen';
import ReceiptScreen from '../screens/BottomNavigationScreens/ReceiptScreen';

const Stack = createNativeStackNavigator();

const OutpassNavigation = () => {
  return (
    <Stack.Navigator screenOptions={{headerShown: false}}>
      <Stack.Screen name="OutpassScreenMain" component={OutpassScreen} />
      <Stack.Screen
        name="CreateOutpassScreen"
        component={CreateOutpassScreen}
      />
      <Stack.Screen name="ReceiptScreen" component={ReceiptScreen} />
      <Stack.Screen
        name={'PrintTemplateScreen'}
        component={PrintTemplateScreen}
      />
    </Stack.Navigator>
  );
};

export default OutpassNavigation;
